package prj.cognitive.ocr;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.gson.annotations.Expose;

import prj.cognitive.utils.Config;

public class AbbyyOcrSettings {
    private static final Boolean CORRECT_RESOLUTION = Config.get(
            AbbyyOcrSettings.class, "CORRECT_RESOLUTION", true
    );

    private static final Integer OVERRIDE_RESOLUTION = Config.get(
            AbbyyOcrSettings.class, "OVERRIDE_RESOLUTION", 0
    );

    private static final Boolean CORRECT_ORIENTATION = Config.get(
            AbbyyOcrSettings.class, "CORRECT_ORIENTATION", false
    );

    private static final Boolean CROP_IMAGE = Config.get(
            AbbyyOcrSettings.class, "CROP_IMAGE", false
    );

    private static final Boolean ENHANCE_LOCAL_CONTRAST = Config.get(
            AbbyyOcrSettings.class, "ENHANCE_LOCAL_CONTRAST", false
    );

    private static final Boolean INVERT_IMAGE = Config.get(
            AbbyyOcrSettings.class, "INVERT_IMAGE", false
    );

    private static final Boolean CORRECT_DISTORTIONS = Config.get(
            AbbyyOcrSettings.class, "CORRECT_DISTORTIONS", false
    );

    private static final Boolean DESKEW_IMAGE = Config.get(
            AbbyyOcrSettings.class, "DESKEW_IMAGE", false
    );

    private static final Boolean REMOVE_GARBAGE = Config.get(
            AbbyyOcrSettings.class, "REMOVE_GARBAGE", false
    );

    private static final Boolean REMOVE_WHITE_NOISE = Config.get(
            AbbyyOcrSettings.class, "REMOVE_WHITE_NOISE", false
    );

    private static final Boolean REMOVE_CORRELATED_NOISE = Config.get(
            AbbyyOcrSettings.class, "REMOVE_CORRELATED_NOISE", false
    );

    private static final Boolean REMOVE_MOTION_BLUR = Config.get(
            AbbyyOcrSettings.class, "REMOVE_MOTION_BLUR", false
    );

    private static final Boolean REMOVE_FULL_OBJECTS = Config.get(
            AbbyyOcrSettings.class, "REMOVE_FULL_OBJECTS", false
    );

    private static final Boolean REMOVE_BACKGROUND_OBJECTS = Config.get(
            AbbyyOcrSettings.class, "REMOVE_BACKGROUND_OBJECTS", false
    );

    private static final Boolean REMOVE_STAMP_OBJECTS = Config.get(
            AbbyyOcrSettings.class, "REMOVE_STAMP_OBJECTS", false
    );

    private static final Boolean TEXT_EXTRACT_MODE = Config.get(
            AbbyyOcrSettings.class, "TEXT_EXTRACT_MODE", false
    );

    private static final Boolean AGGRESSIVE_TEXT_EXTRACTION = Config.get(
            AbbyyOcrSettings.class, "AGGRESSIVE_TEXT_EXTRACTION", false
    );

    private static final Boolean REMOVE_BLUE_COLOR = Config.get(
            AbbyyOcrSettings.class, "REMOVE_BLUE_COLOR", false
    );

    private static final Boolean REMOVE_RED_COLOR = Config.get(
            AbbyyOcrSettings.class, "REMOVE_RED_COLOR", false
    );

    private static final Boolean REMOVE_GREEN_COLOR = Config.get(
            AbbyyOcrSettings.class, "REMOVE_GREEN_COLOR", false
    );

    private static final Boolean REMOVE_YELLOW_COLOR = Config.get(
            AbbyyOcrSettings.class, "REMOVE_YELLOW_COLOR", false
    );

    private static final Boolean HAS_LARGE_CHARACTERS = Config.get(
            AbbyyOcrSettings.class, "HAS_LARGE_CHARACTERS", false
    );

    @Expose
    private Boolean correctResolution = CORRECT_RESOLUTION;

    @Expose
    private Integer overrideResolution = OVERRIDE_RESOLUTION;

    @Expose
    private Boolean correctOrientation = CORRECT_ORIENTATION;

    @Expose
    private Boolean cropImage = CROP_IMAGE;

    @Expose
    private Boolean enhanceLocalContrast = ENHANCE_LOCAL_CONTRAST;

    @Expose
    private Boolean invertImage = INVERT_IMAGE;

    @Expose
    private Boolean correctDistortions = CORRECT_DISTORTIONS;

    @Expose
    private Boolean deskewImage = DESKEW_IMAGE;

    @Expose
    private Boolean removeGarbage = REMOVE_GARBAGE;

    @Expose
    private Boolean removeWhiteNoise = REMOVE_WHITE_NOISE;

    @Expose
    private Boolean removeCorrelatedNoise = REMOVE_CORRELATED_NOISE;

    @Expose
    private Boolean removeMotionBlur = REMOVE_MOTION_BLUR;

    @Expose
    private Boolean removeFullObjects = REMOVE_FULL_OBJECTS;

    @Expose
    private Boolean removeStampObjects = REMOVE_STAMP_OBJECTS;

    @Expose
    private Boolean removeBackgroundObjects = REMOVE_BACKGROUND_OBJECTS;

    @Expose
    private Boolean removeBlueColor = REMOVE_BLUE_COLOR;

    @Expose
    private Boolean removeRedColor = REMOVE_RED_COLOR;

    @Expose
    private Boolean removeGreenColor = REMOVE_GREEN_COLOR;

    @Expose
    private Boolean removeYellowColor = REMOVE_YELLOW_COLOR;

    @Expose
    private Boolean textExtractMode = TEXT_EXTRACT_MODE;

    @Expose
    private Boolean aggressiveTextExtraction = AGGRESSIVE_TEXT_EXTRACTION;

    @Expose
    private Boolean hasLargeCharacters = HAS_LARGE_CHARACTERS;

    public AbbyyOcrSettings() {

    }

    public AbbyyOcrSettings(Boolean correctResolution, Integer overrideResolution, Boolean correctOrientation, Boolean cropImage, Boolean enhanceLocalContrast, Boolean invertImage, Boolean correctDistortions, Boolean deskewImage, Boolean removeGarbage, Boolean removeWhiteNoise, Boolean removeCorrelatedNoise, Boolean removeMotionBlur, Boolean removeFullObjects, Boolean removeStampObjects, Boolean removeBackgroundObjects, Boolean removeBlueColor, Boolean removeRedColor, Boolean removeGreenColor, Boolean removeYellowColor, Boolean textExtractMode, Boolean aggressiveTextExtraction, Boolean hasLargeCharacters) {
        this.correctResolution = correctResolution;
        this.overrideResolution = overrideResolution;
        this.correctOrientation = correctOrientation;
        this.cropImage = cropImage;
        this.enhanceLocalContrast = enhanceLocalContrast;
        this.invertImage = invertImage;
        this.correctDistortions = correctDistortions;
        this.deskewImage = deskewImage;
        this.removeGarbage = removeGarbage;
        this.removeWhiteNoise = removeWhiteNoise;
        this.removeCorrelatedNoise = removeCorrelatedNoise;
        this.removeMotionBlur = removeMotionBlur;
        this.removeFullObjects = removeFullObjects;
        this.removeStampObjects = removeStampObjects;
        this.removeBackgroundObjects = removeBackgroundObjects;
        this.removeBlueColor = removeBlueColor;
        this.removeRedColor = removeRedColor;
        this.removeGreenColor = removeGreenColor;
        this.removeYellowColor = removeYellowColor;
        this.textExtractMode = textExtractMode;
        this.aggressiveTextExtraction = aggressiveTextExtraction;
        this.hasLargeCharacters = hasLargeCharacters;
    }

    public Boolean getCorrectResolution() {
        return correctResolution;
    }

    public void setCorrectResolution(Boolean correctResolution) {
        this.correctResolution = correctResolution;
    }

    public Integer getOverrideResolution() {
        return overrideResolution;
    }

    public void setOverrideResolution(Integer overrideResolution) {
        this.overrideResolution = overrideResolution;
    }

    public Boolean getCorrectOrientation() {
        return correctOrientation;
    }

    public void setCorrectOrientation(Boolean correctOrientation) {
        this.correctOrientation = correctOrientation;
    }

    public Boolean getCropImage() {
        return cropImage;
    }

    public void setCropImage(Boolean cropImage) {
        this.cropImage = cropImage;
    }

    public Boolean getEnhanceLocalContrast() {
        return enhanceLocalContrast;
    }

    public void setEnhanceLocalContrast(Boolean enhanceLocalContrast) {
        this.enhanceLocalContrast = enhanceLocalContrast;
    }

    public Boolean getInvertImage() {
        return invertImage;
    }

    public void setInvertImage(Boolean invertImage) {
        this.invertImage = invertImage;
    }

    public Boolean getCorrectDistortions() {
        return correctDistortions;
    }

    public void setCorrectDistortions(Boolean correctDistortions) {
        this.correctDistortions = correctDistortions;
    }

    public Boolean getDeskewImage() {
        return deskewImage;
    }

    public void setDeskewImage(Boolean deskewImage) {
        this.deskewImage = deskewImage;
    }

    public Boolean getRemoveGarbage() {
        return removeGarbage;
    }

    public void setRemoveGarbage(Boolean removeGarbage) {
        this.removeGarbage = removeGarbage;
    }

    public Boolean getRemoveWhiteNoise() {
        return removeWhiteNoise;
    }

    public void setRemoveWhiteNoise(Boolean removeWhiteNoise) {
        this.removeWhiteNoise = removeWhiteNoise;
    }

    public Boolean getRemoveCorrelatedNoise() {
        return removeCorrelatedNoise;
    }

    public void setRemoveCorrelatedNoise(Boolean removeCorrelatedNoise) {
        this.removeCorrelatedNoise = removeCorrelatedNoise;
    }

    public Boolean getRemoveMotionBlur() {
        return removeMotionBlur;
    }

    public void setRemoveMotionBlur(Boolean removeMotionBlur) {
        this.removeMotionBlur = removeMotionBlur;
    }

    public Boolean getRemoveFullObjects() {
        return removeFullObjects;
    }

    public void setRemoveFullObjects(Boolean removeFullObjects) {
        this.removeFullObjects = removeFullObjects;
    }

    public Boolean getRemoveStampObjects() {
        return removeStampObjects;
    }

    public void setRemoveStampObjects(Boolean removeStampObjects) {
        this.removeStampObjects = removeStampObjects;
    }

    public Boolean getRemoveBackgroundObjects() {
        return removeBackgroundObjects;
    }

    public void setRemoveBackgroundObjects(Boolean removeBackgroundObjects) {
        this.removeBackgroundObjects = removeBackgroundObjects;
    }

    public Boolean getRemoveBlueColor() {
        return removeBlueColor;
    }

    public void setRemoveBlueColor(Boolean removeBlueColor) {
        this.removeBlueColor = removeBlueColor;
    }

    public Boolean getRemoveRedColor() {
        return removeRedColor;
    }

    public void setRemoveRedColor(Boolean removeRedColor) {
        this.removeRedColor = removeRedColor;
    }

    public Boolean getRemoveGreenColor() {
        return removeGreenColor;
    }

    public void setRemoveGreenColor(Boolean removeGreenColor) {
        this.removeGreenColor = removeGreenColor;
    }

    public Boolean getRemoveYellowColor() {
        return removeYellowColor;
    }

    public void setRemoveYellowColor(Boolean removeYellowColor) {
        this.removeYellowColor = removeYellowColor;
    }

    public Boolean getTextExtractMode() {
        return textExtractMode;
    }

    public void setTextExtractMode(Boolean textExtractMode) {
        this.textExtractMode = textExtractMode;
    }

    public Boolean getAggressiveTextExtraction() {
        return aggressiveTextExtraction;
    }

    public void setAggressiveTextExtraction(Boolean aggressiveTextExtraction) {
        this.aggressiveTextExtraction = aggressiveTextExtraction;
    }

    public Boolean getHasLargeCharacters() {
        return hasLargeCharacters;
    }

    public void setHasLargeCharacters(Boolean hasLargeCharacters) {
        this.hasLargeCharacters = hasLargeCharacters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AbbyyOcrSettings)) return false;
        AbbyyOcrSettings that = (AbbyyOcrSettings) o;
        return Objects.equal(correctResolution, that.correctResolution) &&
                Objects.equal(overrideResolution, that.overrideResolution) &&
                Objects.equal(correctOrientation, that.correctOrientation) &&
                Objects.equal(cropImage, that.cropImage) &&
                Objects.equal(enhanceLocalContrast, that.enhanceLocalContrast) &&
                Objects.equal(invertImage, that.invertImage) &&
                Objects.equal(correctDistortions, that.correctDistortions) &&
                Objects.equal(deskewImage, that.deskewImage) &&
                Objects.equal(removeGarbage, that.removeGarbage) &&
                Objects.equal(removeWhiteNoise, that.removeWhiteNoise) &&
                Objects.equal(removeCorrelatedNoise, that.removeCorrelatedNoise) &&
                Objects.equal(removeMotionBlur, that.removeMotionBlur) &&
                Objects.equal(removeFullObjects, that.removeFullObjects) &&
                Objects.equal(removeStampObjects, that.removeStampObjects) &&
                Objects.equal(removeBackgroundObjects, that.removeBackgroundObjects) &&
                Objects.equal(removeBlueColor, that.removeBlueColor) &&
                Objects.equal(removeRedColor, that.removeRedColor) &&
                Objects.equal(removeGreenColor, that.removeGreenColor) &&
                Objects.equal(removeYellowColor, that.removeYellowColor) &&
                Objects.equal(textExtractMode, that.textExtractMode) &&
                Objects.equal(aggressiveTextExtraction, that.aggressiveTextExtraction) &&
                Objects.equal(hasLargeCharacters, that.hasLargeCharacters);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(correctResolution, overrideResolution, correctOrientation, cropImage, enhanceLocalContrast, invertImage, correctDistortions, deskewImage, removeGarbage, removeWhiteNoise, removeCorrelatedNoise, removeMotionBlur, removeFullObjects, removeStampObjects, removeBackgroundObjects, removeBlueColor, removeRedColor, removeGreenColor, removeYellowColor, textExtractMode, aggressiveTextExtraction, hasLargeCharacters);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("correctResolution", correctResolution)
                .add("overrideResolution", overrideResolution)
                .add("correctOrientation", correctOrientation)
                .add("cropImage", cropImage)
                .add("enhanceLocalContrast", enhanceLocalContrast)
                .add("invertImage", invertImage)
                .add("correctDistortions", correctDistortions)
                .add("deskewImage", deskewImage)
                .add("removeGarbage", removeGarbage)
                .add("removeWhiteNoise", removeWhiteNoise)
                .add("removeCorrelatedNoise", removeCorrelatedNoise)
                .add("removeMotionBlur", removeMotionBlur)
                .add("removeFullObjects", removeFullObjects)
                .add("removeStampObjects", removeStampObjects)
                .add("removeBackgroundObjects", removeBackgroundObjects)
                .add("removeBlueColor", removeBlueColor)
                .add("removeRedColor", removeRedColor)
                .add("removeGreenColor", removeGreenColor)
                .add("removeYellowColor", removeYellowColor)
                .add("textExtractMode", textExtractMode)
                .add("aggressiveTextExtraction", aggressiveTextExtraction)
                .add("hasLargeCharacters", hasLargeCharacters)
                .toString();
    }
}

