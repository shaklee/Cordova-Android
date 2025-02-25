/*
 * PSPDFCordovaPlugin.java
 *
 *   PSPDFKit
 *
 *   Copyright (c) 2015 PSPDFKit GmbH. All rights reserved.
 *
 *   THIS SOURCE CODE AND ANY ACCOMPANYING DOCUMENTATION ARE PROTECTED BY INTERNATIONAL COPYRIGHT LAW
 *   AND MAY NOT BE RESOLD OR REDISTRIBUTED. USAGE IS BOUND TO THE PSPDFKIT LICENSE AGREEMENT.
 *   UNAUTHORIZED REPRODUCTION OR DISTRIBUTION IS SUBJECT TO CIVIL AND CRIMINAL PENALTIES.
 *   This notice may not be removed from this file.
 */

package com.pspdfkit.cordova;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.pspdfkit.PSPDFKit;
import com.pspdfkit.configuration.activity.PSPDFActivityConfiguration;
import com.pspdfkit.configuration.annotations.AnnotationEditingConfiguration;
import com.pspdfkit.configuration.page.PageFitMode;
import com.pspdfkit.configuration.page.PageScrollDirection;
import com.pspdfkit.ui.PSPDFActivity;
import com.pspdfkit.ui.PSPDFAppCompatActivity;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Iterator;

public class PSPDFCordovaPlugin extends CordovaPlugin {

    private static final String METADATA_LICENSE_KEY = "PSPDFKIT_LICENSE_KEY";

    private static final int ARG_DOCUMENT_URI = 0;
    private static final int ARG_OPTIONS = 1;

    private String licenseKey;

    @Override public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);

        try {
            licenseKey = cordova.getActivity().getPackageManager().getApplicationInfo(cordova.getActivity().getPackageName(),
                PackageManager.GET_ACTIVITIES | PackageManager.GET_META_DATA).metaData.getString(METADATA_LICENSE_KEY, null);
        } catch (PackageManager.NameNotFoundException e) {
            throw new PSPDFCordovaPluginException("Error while reading PSPDFKit license from AndroidManifest.xml", e);
        }

        if (TextUtils.isEmpty(licenseKey)) {
            throw new PSPDFCordovaPluginException("PSPDFKit license key is missing! Please add a <meta-data android:name=\"PSPDFKIT_LICENSE_KEY\" android:value=\"...\"> to your AndroidManifest.xml.");
        }

        try {
            PSPDFKit.initialize(cordova.getActivity(), licenseKey);
        } catch (Exception ex) {
            throw new PSPDFCordovaPluginException("Error while initializing PSPDFKit", ex);
        }
    }

    @Override public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        final PSPDFActivityConfiguration configuration = parseOptionsToConfiguration(args.getJSONObject(ARG_OPTIONS));

        if (action.equals("showDocument")) {
            final Uri documentUri = Uri.parse(args.getString(ARG_DOCUMENT_URI));
            this.showDocument(documentUri, configuration, callbackContext);
            return true;
        } else if (action.equals("showDocumentFromAssets")) {
            this.showDocumentFromAssets(args.getString(ARG_DOCUMENT_URI), configuration, callbackContext);
            return true;
        }

        return false;
    }

    @SuppressWarnings("ConstantConditions")
    @NonNull private PSPDFActivityConfiguration parseOptionsToConfiguration(@NonNull final JSONObject options) throws JSONException {
        final Activity activity = cordova.getActivity();
        final PSPDFActivityConfiguration.Builder builder = new PSPDFActivityConfiguration.Builder(activity, licenseKey);
        final Iterator<String> optionIterator = options.keys();

        while (optionIterator.hasNext()) {
            final String option = optionIterator.next();
            final Object value = options.get(option);

            try {
                if ("backgroundColor".equals(option)) {
                    builder.backgroundColor(Color.parseColor((String) value));
                } else if ("disableOutline".equals(option) && ((Boolean) value)) {
                    builder.disableOutline();
                } else if ("disableSearch".equals(option) && ((Boolean) value)) {
                    builder.disableSearch();
                } else if ("hidePageLabels".equals(option) && ((Boolean) value)) {
                    builder.hidePageLabels();
                } else if ("hidePageNumberOverlay".equals(option) && ((Boolean) value)) {
                    builder.hidePageNumberOverlay();
                } else if ("hideThumbnailBar".equals(option) && ((Boolean) value)) {
                    builder.hideThumbnailBar();
                } else if ("hideThumbnailGrid".equals(option) && ((Boolean) value)) {
                    builder.hideThumbnailGrid();
                } else if ("diskCacheSize".equals(option)) {
                    builder.diskCacheSize((Integer) value);
                } else if ("memoryCacheSize".equals(option)) {
                    builder.memoryCacheSize((Integer) value);
                } else if ("pageFitMode".equals(option)) {
                    builder.fitMode(PageFitMode.valueOf((String) value));
                } else if ("scrollDirection".equals(option)) {
                    builder.scrollDirection(PageScrollDirection.valueOf((String) value));
                } else if ("invertColors".equals(option)) {
                    builder.invertColors((Boolean) value);
                } else if ("toGrayscale".equals(option)) {
                    builder.toGrayscale((Boolean) value);
                } else if ("loggingEnabled".equals(option)) {
                    builder.loggingEnabled((Boolean) value);
                } else if ("title".equals(option)) {
                    builder.title(fromJsonString(options.getString("title")));
                } else if ("startZoomScale".equals(option)) {
                    builder.startZoomScale((float) options.getDouble("startZoomScale"));
                } else if ("maxZoomScale".equals(option)) {
                    builder.maxZoomScale((float) options.getDouble("maxZoomScale"));
                } else if ("zoomOutBounce".equals(option)) {
                    builder.zoomOutBounce(options.getBoolean("zoomOutBounce"));
                } else if ("page".equals(option)) {
                    builder.page(options.getInt("page"));
                } else if ("useImmersiveMode".equals(option)) {
                    builder.useImmersiveMode(options.getBoolean("useImmersiveMode"));
                } else if ("searchType".equals(option)) {
                    final String searchType = options.getString("searchType");
                    if ("SEARCH_INLINE".equals(searchType)) builder.setSearchType(PSPDFActivityConfiguration.SEARCH_INLINE);
                    else if ("SEARCH_MODULAR".equals(searchType)) builder.setSearchType(PSPDFActivityConfiguration.SEARCH_MODULAR);
                    else throw new IllegalArgumentException(String.format("Invalid search type: %s", value));
                } else if ("autosaveEnabled".equals(option)) {
                    builder.autosaveEnabled(options.getBoolean("autosaveEnabled"));
                } else if ("annotationEditing".equals(option)) {
                    final AnnotationEditingConfiguration.Builder annotationBuilder = new AnnotationEditingConfiguration.Builder(activity);
                    final JSONObject annotationEditing = options.getJSONObject("annotationEditing");
                    final Iterator<String> annotationOptionIterator = annotationEditing.keys();

                    while (annotationOptionIterator.hasNext()) {
                        final String annotationEditingOption = annotationOptionIterator.next();
                        final Object annotationEditingValue = annotationEditing.get(annotationEditingOption);

                        if ("enabled".equals(annotationEditingOption)) {
                            if ((Boolean) annotationEditingValue) annotationBuilder.enableAnnotationEditing();
                            else annotationBuilder.disableAnnotationEditing();
                        } else if ("creatorName".equals(annotationEditingOption)) {
                            annotationBuilder.defaultAnnotationCreator(fromJsonString(annotationEditing.getString("creatorName")));
                        } else {
                            throw new IllegalArgumentException(String.format("Invalid annotation editing option '%s'", annotationEditingOption));
                        }
                    }

                    builder.annotationEditingConfiguration(annotationBuilder.build());
                } else {
                    throw new IllegalArgumentException(String.format("Invalid plugin option '%s'", option));
                }
            } catch (Exception ex) {
                throw new PSPDFCordovaPluginException(String.format("Error while parsing option '%s'", option), ex);
            }
        }

        return builder.build();
    }

    /**
     * Ensures that Javascript "null" strings are correctly converted to javas <code>null</code>.
     */
    @Nullable private String fromJsonString(@Nullable String creatorName) {
        if (creatorName == null || creatorName.equals("null")) return null;
        return creatorName;
    }

    /**
     * Starts the {@link PSPDFActivity} to show a single document.
     *
     * @param documentUri     Local filesystem Uri pointing to a document.
     * @param configuration   PSPDFKit configuration.
     * @param callbackContext Cordova callback.
     */

    private void showDocument(@NonNull Uri documentUri, @NonNull final PSPDFActivityConfiguration configuration,
                              @NonNull final CallbackContext callbackContext) {
        showDocumentForUri(documentUri, configuration);
        callbackContext.success();
    }

    /**
     * Starts the {@link PSPDFActivity} to show a single document stored within the app's assets.
     *
     * @param assetPath       Relative path inside the app's assets folder.
     * @param configuration   PSPDFKit configuration.
     * @param callbackContext Cordova callback.
     */
    private void showDocumentFromAssets(@NonNull final String assetPath, @NonNull final PSPDFActivityConfiguration configuration,
                                        @NonNull final CallbackContext callbackContext) {
        ExtractAssetTask.extract(assetPath, cordova.getActivity(), new ExtractAssetTask.OnDocumentExtractedCallback() {
            @Override
            public void onDocumentExtracted(File documentFile) {
                if (documentFile != null) {
                    showDocumentForUri(Uri.fromFile(documentFile), configuration);
                    callbackContext.success();
                } else {
                    callbackContext.error("Could not load '" + assetPath + "' from the assets.");
                }
            }
        });
    }

    private void showDocumentForUri(@NonNull Uri uri, @NonNull final PSPDFActivityConfiguration configuration) {
        PSPDFAppCompatActivity.showDocument(cordova.getActivity(), uri, configuration);
    }


}
