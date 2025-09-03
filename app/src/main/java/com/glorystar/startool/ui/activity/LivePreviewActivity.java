/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.glorystar.startool.ui.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.glorystar.startool.R;
import com.glorystar.startool.ui.camera.CameraSource;
import com.glorystar.startool.ui.camera.CameraSourcePreview;
import com.glorystar.startool.ui.ml.GraphicOverlay;
import com.glorystar.startool.ui.ml.PreferenceUtils;
import com.glorystar.startool.ui.ml.SettingsActivity;
import com.glorystar.startool.ui.ml.barcodescanner.BarcodeScannerProcessor;
import com.glorystar.startool.ui.ml.facedetector.FaceDetectorProcessor;
import com.glorystar.startool.ui.ml.facemeshdetector.FaceMeshDetectorProcessor;
import com.glorystar.startool.ui.ml.labeldetector.LabelDetectorProcessor;
import com.glorystar.startool.ui.ml.objectdetector.ObjectDetectorProcessor;
import com.glorystar.startool.ui.ml.posedetector.PoseDetectorProcessor;
import com.glorystar.startool.ui.ml.segmenter.SegmenterProcessor;
import com.glorystar.startool.ui.ml.textdetector.TextRecognitionProcessor;
import com.google.android.gms.common.annotation.KeepName;
import com.google.mlkit.common.model.LocalModel;
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions.ZoomCallback;
import com.google.mlkit.vision.label.custom.CustomImageLabelerOptions;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.objects.custom.CustomObjectDetectorOptions;
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions;
import com.google.mlkit.vision.text.devanagari.DevanagariTextRecognizerOptions;
import com.google.mlkit.vision.text.japanese.JapaneseTextRecognizerOptions;
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/** Live preview demo for ML Kit APIs. */
@KeepName
public final class LivePreviewActivity extends AppCompatActivity
    implements OnItemSelectedListener, CompoundButton.OnCheckedChangeListener {
  private String OBJECT_DETECTION;
  private String OBJECT_DETECTION_CUSTOM;
  private String CUSTOM_AUTOML_OBJECT_DETECTION;
  private String FACE_DETECTION;
  private String BARCODE_SCANNING;
  private String IMAGE_LABELING;
  private String IMAGE_LABELING_CUSTOM;
  private String CUSTOM_AUTOML_LABELING;
  private String POSE_DETECTION;
  private String SELFIE_SEGMENTATION;
  private String TEXT_RECOGNITION_LATIN;
  private String TEXT_RECOGNITION_CHINESE;
  private String TEXT_RECOGNITION_DEVANAGARI;
  private String TEXT_RECOGNITION_JAPANESE;
  private String TEXT_RECOGNITION_KOREAN;
  private String FACE_MESH_DETECTION;

  private static final String TAG = "LivePreviewActivity";

  private CameraSource cameraSource = null;
  private CameraSourcePreview preview;
  private ToggleButton facingSwitch;
  private GraphicOverlay graphicOverlay;
  private int selectedModel = 0;
  private LinearLayout linearBack;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Log.d(TAG, "onCreate");

    setContentView(R.layout.activity_vision_live_preview);

    OBJECT_DETECTION = getString(R.string.ml_1);
    OBJECT_DETECTION_CUSTOM = getString(R.string.ml_2);
    CUSTOM_AUTOML_OBJECT_DETECTION = getString(R.string.ml_3);
    FACE_DETECTION = getString(R.string.ml_4);
    BARCODE_SCANNING = getString(R.string.ml_5);
    IMAGE_LABELING = getString(R.string.ml_6);
    IMAGE_LABELING_CUSTOM = getString(R.string.ml_7);
    CUSTOM_AUTOML_LABELING = getString(R.string.ml_8);
    POSE_DETECTION = getString(R.string.ml_9);
    SELFIE_SEGMENTATION = getString(R.string.ml_10);
    TEXT_RECOGNITION_LATIN = getString(R.string.ml_11);
    TEXT_RECOGNITION_CHINESE = getString(R.string.ml_12);
    TEXT_RECOGNITION_DEVANAGARI = getString(R.string.ml_13);
    TEXT_RECOGNITION_JAPANESE = getString(R.string.ml_14);
    TEXT_RECOGNITION_KOREAN = getString(R.string.ml_15);
    FACE_MESH_DETECTION = getString(R.string.ml_16);

    facingSwitch = findViewById(R.id.facing_switch);
    preview = findViewById(R.id.preview_view);
    if (preview == null) {
      Log.d(TAG, "Preview is null");
    }
    graphicOverlay = findViewById(R.id.graphic_overlay);
    if (graphicOverlay == null) {
      Log.d(TAG, "graphicOverlay is null");
    }
    facingSwitch.setOnCheckedChangeListener(this);
    linearBack = findViewById(R.id.linear_back);
    linearBack.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        finish();
      }
    });

    Spinner spinner = findViewById(R.id.spinner);
    List<String> options = new ArrayList<>();
    options.add(OBJECT_DETECTION);
    options.add(OBJECT_DETECTION_CUSTOM);
    options.add(CUSTOM_AUTOML_OBJECT_DETECTION);
    options.add(FACE_DETECTION);
    options.add(BARCODE_SCANNING);
    options.add(IMAGE_LABELING);
    options.add(IMAGE_LABELING_CUSTOM);
    options.add(CUSTOM_AUTOML_LABELING);
    options.add(POSE_DETECTION);
    options.add(SELFIE_SEGMENTATION);
    options.add(TEXT_RECOGNITION_LATIN);
    options.add(TEXT_RECOGNITION_CHINESE);
    options.add(TEXT_RECOGNITION_DEVANAGARI);
    options.add(TEXT_RECOGNITION_JAPANESE);
    options.add(TEXT_RECOGNITION_KOREAN);
    options.add(FACE_MESH_DETECTION);

    // Creating adapter for spinner
    ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_style, options);
    // Drop down layout style - list view with radio button
    dataAdapter.setDropDownViewResource(R.layout.spinner_style);
    // attaching data adapter to spinner
    spinner.setAdapter(dataAdapter);
    spinner.setOnItemSelectedListener(this);

//    ToggleButton facingSwitch = findViewById(R.id.facing_switch);
//    facingSwitch.setOnCheckedChangeListener(this);

    ImageView settingsButton = findViewById(R.id.settings_button);
    settingsButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
          intent.putExtra(
              SettingsActivity.EXTRA_LAUNCH_SOURCE, SettingsActivity.LaunchSource.LIVE_PREVIEW);
          startActivity(intent);
        });

    createCameraSource(selectedModel);
  }

  @Override
  public synchronized void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    // An item was selected. You can retrieve the selected item using
    // parent.getItemAtPosition(pos)
    selectedModel = pos;
    Log.d(TAG, "Selected model: " + parent.getItemAtPosition(pos).toString());
    preview.stop();
    createCameraSource(selectedModel);
    startCameraSource();
  }

  @Override
  public void onNothingSelected(AdapterView<?> parent) {
    // Do nothing.
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    Log.d(TAG, "Set facing");
    if (cameraSource != null) {
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      }
      preview.stop();
      startCameraSource();
    }else {
      createCameraSource(selectedModel);
      if (isChecked) {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_BACK);
      } else {
        cameraSource.setFacing(CameraSource.CAMERA_FACING_FRONT);
      }
      startCameraSource();
    }
  }

  private void createCameraSource(int model) {
    // If there's no existing cameraSource, create one.
    if (cameraSource == null) {
      cameraSource = new CameraSource(this, graphicOverlay);
    }

    try {
      switch (model) {
        case 0:
          Log.i(TAG, "Using Object Detector Processor");
          ObjectDetectorOptions objectDetectorOptions =
              PreferenceUtils.getObjectDetectorOptionsForLivePreview(this);
          cameraSource.setMachineLearningFrameProcessor(
              new ObjectDetectorProcessor(this, objectDetectorOptions));
          break;
        case 1:
          Log.i(TAG, "Using Custom Object Detector Processor");
          LocalModel localModel =
              new LocalModel.Builder()
                  .setAssetFilePath("custom_models/object_labeler.tflite")
                  .build();
          CustomObjectDetectorOptions customObjectDetectorOptions =
              PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(this, localModel);
          cameraSource.setMachineLearningFrameProcessor(
              new ObjectDetectorProcessor(this, customObjectDetectorOptions));
          break;
        case 2:
          Log.i(TAG, "Using Custom AutoML Object Detector Processor");
          LocalModel customAutoMLODTLocalModel =
              new LocalModel.Builder().setAssetManifestFilePath("automl/manifest.json").build();
          CustomObjectDetectorOptions customAutoMLODTOptions =
              PreferenceUtils.getCustomObjectDetectorOptionsForLivePreview(
                  this, customAutoMLODTLocalModel);
          cameraSource.setMachineLearningFrameProcessor(
              new ObjectDetectorProcessor(this, customAutoMLODTOptions));
          break;
        case 3:
          Log.i(TAG, "Using Face Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(new FaceDetectorProcessor(this));
          break;
        case 4:
          Log.i(TAG, "Using Barcode Detector Processor");
          ZoomCallback zoomCallback = null;
          if (PreferenceUtils.shouldEnableAutoZoom(this)) {
            zoomCallback = zoomLevel -> cameraSource.setZoom(zoomLevel);
          }
          cameraSource.setMachineLearningFrameProcessor(
                  new BarcodeScannerProcessor(this, zoomCallback));
          break;
        case 5:
          Log.i(TAG, "Using Image Label Detector Processor");
          cameraSource.setMachineLearningFrameProcessor(
                  new LabelDetectorProcessor(this, ImageLabelerOptions.DEFAULT_OPTIONS));
          break;
        case 6:
          Log.i(TAG, "Using Custom Image Label Detector Processor");
          LocalModel localClassifier =
                  new LocalModel.Builder()
                          .setAssetFilePath("custom_models/bird_classifier.tflite")
                          .build();
          CustomImageLabelerOptions customImageLabelerOptions =
                  new CustomImageLabelerOptions.Builder(localClassifier).build();
          cameraSource.setMachineLearningFrameProcessor(
                  new LabelDetectorProcessor(this, customImageLabelerOptions));
          break;
        case 7:
          Log.i(TAG, "Using Custom AutoML Image Label Detector Processor");
          LocalModel customAutoMLLabelLocalModel =
                  new LocalModel.Builder().setAssetManifestFilePath("automl/manifest.json").build();
          CustomImageLabelerOptions customAutoMLLabelOptions =
                  new CustomImageLabelerOptions.Builder(customAutoMLLabelLocalModel)
                          .setConfidenceThreshold(0)
                          .build();
          cameraSource.setMachineLearningFrameProcessor(
                  new LabelDetectorProcessor(this, customAutoMLLabelOptions));
          break;
        case 8:
          PoseDetectorOptionsBase poseDetectorOptions =
                  PreferenceUtils.getPoseDetectorOptionsForLivePreview(this);
          Log.i(TAG, "Using Pose Detector with options " + poseDetectorOptions);
          boolean shouldShowInFrameLikelihood =
                  PreferenceUtils.shouldShowPoseDetectionInFrameLikelihoodLivePreview(this);
          boolean visualizeZ = PreferenceUtils.shouldPoseDetectionVisualizeZ(this);
          boolean rescaleZ = PreferenceUtils.shouldPoseDetectionRescaleZForVisualization(this);
          boolean runClassification = PreferenceUtils.shouldPoseDetectionRunClassification(this);
          cameraSource.setMachineLearningFrameProcessor(
                  new PoseDetectorProcessor(
                          this,
                          poseDetectorOptions,
                          shouldShowInFrameLikelihood,
                          visualizeZ,
                          rescaleZ,
                          runClassification,
                          /* isStreamMode = */ true));
          break;
        case 9:
          cameraSource.setMachineLearningFrameProcessor(new SegmenterProcessor(this));
          break;
        case 10:
          Log.i(TAG, "Using on-device Text recognition Processor for Latin.");
          cameraSource.setMachineLearningFrameProcessor(
              new TextRecognitionProcessor(this, new TextRecognizerOptions.Builder().build()));
          break;
        case 11:
          Log.i(TAG, "Using on-device Text recognition Processor for Latin and Chinese.");
          cameraSource.setMachineLearningFrameProcessor(
              new TextRecognitionProcessor(
                  this, new ChineseTextRecognizerOptions.Builder().build()));
          break;
        case 12:
          Log.i(TAG, "Using on-device Text recognition Processor for Latin and Devanagari.");
          cameraSource.setMachineLearningFrameProcessor(
              new TextRecognitionProcessor(
                  this, new DevanagariTextRecognizerOptions.Builder().build()));
          break;
        case 13:
          Log.i(TAG, "Using on-device Text recognition Processor for Latin and Japanese.");
          cameraSource.setMachineLearningFrameProcessor(
              new TextRecognitionProcessor(
                  this, new JapaneseTextRecognizerOptions.Builder().build()));
          break;
        case 14:
          Log.i(TAG, "Using on-device Text recognition Processor for Latin and Korean.");
          cameraSource.setMachineLearningFrameProcessor(
              new TextRecognitionProcessor(
                  this, new KoreanTextRecognizerOptions.Builder().build()));
          break;
        case 15:
          cameraSource.setMachineLearningFrameProcessor(new FaceMeshDetectorProcessor(this));
          break;
        default:
          Log.e(TAG, "Unknown model: " + model);
      }
    } catch (RuntimeException e) {
      Log.e(TAG, "Can not create image processor: " + model, e);
      Toast.makeText(
              getApplicationContext(),
              "Can not create image processor: " + e.getMessage(),
              Toast.LENGTH_LONG)
          .show();
    }
  }

  /**
   * Starts or restarts the camera source, if it exists. If the camera source doesn't exist yet
   * (e.g., because onResume was called before the camera source was created), this will be called
   * again when the camera source is created.
   */
  private void startCameraSource() {
    if (cameraSource != null) {
      try {
        if (preview == null) {
          Log.d(TAG, "resume: Preview is null");
        }
        if (graphicOverlay == null) {
          Log.d(TAG, "resume: graphOverlay is null");
        }
        preview.start(cameraSource, graphicOverlay);
      } catch (IOException e) {
        Log.e(TAG, "Unable to start camera source.", e);
        cameraSource.release();
        cameraSource = null;
      }
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    Log.d(TAG, "onResume");
    createCameraSource(selectedModel);
    startCameraSource();
  }

  /** Stops the camera. */
  @Override
  protected void onPause() {
    super.onPause();
    preview.stop();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    if (cameraSource != null) {
      cameraSource.release();
    }
  }
}
