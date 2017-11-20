package com.example.yifanyang.tensorflowdemo;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.RectF;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifanyang on 2017/11/20.
 */

public class ObjectDetector {
    private static final int MAX_RESULTS = 100;
    private static final int INPUT_SIZE = 300;
    private  String  labelFilename;
    private String modelFilename;
    private List<String> labels = new ArrayList<>();
    private AssetManager assetManager;
    private TensorFlowInferenceInterface inferenceInterface;

    public  ObjectDetector(String labelFilename,String modelFilename,AssetManager assetManager){
        this.labelFilename = labelFilename;
        this.modelFilename=modelFilename;
        this.assetManager = assetManager;
    }
    public  void load () throws IOException{
        InputStream labelsInput = assetManager.open(labelFilename);
        BufferedReader br = new BufferedReader(new InputStreamReader(labelsInput));
        String line;
        while ((line = br.readLine())!=null){
            labels.add(line);
        }
        br.close();
        if (inferenceInterface != null){
            inferenceInterface.close();
        }
        inferenceInterface = new TensorFlowInferenceInterface(assetManager,modelFilename);
    }
    public List<DetectionResult> detect(Bitmap bitmap, float minimum) {
        int[] pixels = new int[INPUT_SIZE * INPUT_SIZE];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        byte[] byteInput = new byte[pixels.length * 3];
        for (int i = 0; i < pixels.length; ++i) {
            byteInput[i * 3 + 2] = (byte) (pixels[i] & 0xFF);
            byteInput[i * 3 + 1] = (byte) ((pixels[i] >> 8) & 0xFF);
            byteInput[i * 3 + 0] = (byte) ((pixels[i] >> 16) & 0xFF);
        }
        inferenceInterface.feed("image_tensor", byteInput, 1, INPUT_SIZE, INPUT_SIZE, 3);
        inferenceInterface.run(new String[]{"detection_boxes", "detection_scores",
                "detection_classes"}, false);
        float[] boxes = new float[MAX_RESULTS * 4];
        float[] scores = new float[MAX_RESULTS];
        float[] classes = new float[MAX_RESULTS];
        inferenceInterface.fetch("detection_boxes", boxes);
        inferenceInterface.fetch("detection_scores", scores);
        inferenceInterface.fetch("detection_classes", classes);
        List<DetectionResult> results = new ArrayList<>();
        for (int i = 0; i < classes.length; i++) {
            if (scores[i] > minimum) {
                RectF box = new RectF(
                        boxes[4 * i + 1] * INPUT_SIZE,
                        boxes[4 * i] * INPUT_SIZE,
                        boxes[4 * i + 3] * INPUT_SIZE,
                        boxes[4 * i + 2] * INPUT_SIZE);
                results.add(new DetectionResult(labels.get((int) classes[i]), scores[i], box));
            }
        }
        return results;
    }

    public int getInputSize() {
        return INPUT_SIZE;
    }

}
