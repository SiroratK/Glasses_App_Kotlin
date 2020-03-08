package com.example.bitamirshafiee.ml_kit_skeleton;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.os.Trace;
import android.util.Log;

import org.tensorflow.lite.DataType;
import org.tensorflow.lite.Interpreter;
import org.tensorflow.lite.support.common.FileUtil;
import org.tensorflow.lite.support.common.TensorProcessor;
import org.tensorflow.lite.support.image.ImageProcessor;
import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.image.ops.ResizeOp;
import org.tensorflow.lite.support.image.ops.ResizeWithCropOrPadOp;
import org.tensorflow.lite.support.image.ops.Rot90Op;
import org.tensorflow.lite.support.label.TensorLabel;
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Classifier {

    public String modelPath;
    public Interpreter model;

    private int imageSizeX;
    private int imageSizeY;
    private List<String> labels;
    private TensorImage inputImageBuffer;
    private TensorBuffer outputProbabilityBuffer;
    private TensorProcessor probabilityProcessor;
    private String labelPath = "label";

    public Classifier(Context context, String modelPath) {
        this.modelPath = modelPath;
        try {

            MappedByteBuffer tflite_model = loadModelFile(context.getAssets(), modelPath);

            Log.d("Classifier", "Loading model");
            this.model = new Interpreter(tflite_model);
            Log.d("Classifier", "Loaded model");
            int imageTensorIndex = 0;
            int[] imageShape = this.model.getInputTensor(imageTensorIndex).shape();
            imageSizeY = imageShape[1];
            imageSizeX = imageShape[2];
            DataType imageDataType = model.getInputTensor(imageTensorIndex).dataType();
            int probabilityTensorIndex = 0;
            int[] probabilityShape =
                    model.getOutputTensor(probabilityTensorIndex).shape(); // {1, NUM_CLASSES}
            DataType probabilityDataType = model.getOutputTensor(probabilityTensorIndex).dataType();

            inputImageBuffer = new TensorImage(imageDataType);

            outputProbabilityBuffer = TensorBuffer.createFixedSize(probabilityShape, probabilityDataType);

            probabilityProcessor = new TensorProcessor.Builder().build();

            labels = FileUtil.loadLabels(context,labelPath);
            Log.d("Classifier", "Image shape : " + Arrays.toString(imageShape));

        } catch (Exception e) {
            Log.e("Classifier", e.toString());
        }



    }



    private static MappedByteBuffer loadModelFile(AssetManager assets, String modelFilename)
            throws IOException {
        String[] fileList = assets.list("");
        for (String file : fileList) {
            Log.d("Classifier", file);
        }
        AssetFileDescriptor fileDescriptor = assets.openFd(modelFilename);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public String predict(final Bitmap bitmap) {
        Log.d("Classifier", "Inference model");
        inputImageBuffer = loadImage(bitmap);
        model.run(inputImageBuffer.getBuffer(), outputProbabilityBuffer.getBuffer().rewind());
        Log.d("Classifier", "Done !!" + Arrays.toString(outputProbabilityBuffer.getFloatArray()));
        Log.d("Classifier","label = "+labels);
        Map<String, Float> labeledProbability =
                new TensorLabel(labels, probabilityProcessor.process(outputProbabilityBuffer))
                        .getMapWithFloatValue();
        Trace.endSection();
        Log.d("Classifier","result = "+getTopKProbability(labeledProbability));
        return getTopKProbability(labeledProbability);
    }

    private String getTopKProbability(Map<String, Float> labelProb) {
        float Maxvalue = (Collections.max(labelProb.values()));

        for (Map.Entry<String, Float> entry : labelProb.entrySet()) {
            if (entry.getValue()==Maxvalue) {
                String max = entry.getKey();     // Print the key with max value
                return max;
            }
        }
        return "nothing";
    }

    private TensorImage loadImage(final Bitmap bitmap) {
        // Loads bitmap into a TensorImage.
        inputImageBuffer.load(bitmap);

        // Creates processor for the TensorImage.
        int cropSize = Math.min(bitmap.getWidth(), bitmap.getHeight());
        // TODO(b/143564309): Fuse ops inside ImageProcessor.
        ImageProcessor imageProcessor =
                new ImageProcessor.Builder()
                        .add(new ResizeWithCropOrPadOp(cropSize, cropSize))
                        .add(new ResizeOp(imageSizeX, imageSizeY, ResizeOp.ResizeMethod.NEAREST_NEIGHBOR))
                        .build();
        return imageProcessor.process(inputImageBuffer);
    }



}
