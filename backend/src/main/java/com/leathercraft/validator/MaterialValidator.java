package com.leathercraft.validator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MaterialValidator {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final Set<String> VALID_LEATHER_TYPES = new HashSet<>(Arrays.asList(
        "植鞣革", "植鞣", "铬鞣革", "铬鞣", "半植鞣", "油蜡皮", "油皮", "蜡皮",
        "疯马皮", "疯马", "磨砂皮", "反绒皮", "反绒", "纳帕皮", "纳帕", "粒面皮",
        "修面皮", "荔枝纹", "摔纹皮", "压花皮", "鳄鱼纹", "鸵鸟纹", "蛇纹",
        "羊皮", "山羊皮", "绵羊皮", "牛皮", "黄牛皮", "水牛皮", "马皮", "猪皮",
        "鹿皮", "鸵鸟皮", "鳄鱼皮", "蜥蜴皮", "蟒蛇皮", "鲨鱼皮",
        "头层皮", "二层皮", "三层皮", "再生皮", "复合皮", "人造革", "PU", "PVC"
    ));

    private static final Set<String> VALID_COLORS = new HashSet<>(Arrays.asList(
        "原色", "自然色", "本色",
        "黑色", "白色", "红色", "黄色", "蓝色", "绿色", "紫色", "橙色", "粉色", "灰色", "棕色", "褐色",
        "深棕", "浅棕", "红棕", "黄棕", "咖啡", "咖啡色", "巧克力", "巧克力色",
        "深红", "酒红", "枣红", "砖红", "正红", "中国红",
        "深蓝", "藏蓝", "宝蓝", "天蓝", "湖蓝", "孔雀蓝",
        "深绿", "墨绿", "草绿", "军绿", "橄榄绿", "薄荷绿",
        "深黄", "浅黄", "米黄", "杏黄", "鹅黄", "姜黄", "柠檬黄",
        "深灰", "浅灰", "银灰", "炭灰",
        "米白", "乳白", "象牙白", "珍珠白", "雪白",
        "驼色", "米色", "卡其", "卡其色", "杏色", "藕色", "肉色",
        "金色", "银色", "古铜色", "青铜色", "玫瑰金"
    ));

    private static final Set<String> VALID_QUANTITY_UNITS = new HashSet<>(Arrays.asList(
        "张", "片", "块", "码", "英尺", "平方英尺", "sqft", "SF",
        "米", "厘米", "cm", "mm",
        "克", "g", "千克", "kg",
        "个", "套", "组", "卷", "条", "米", "公尺"
    ));

    private static final Pattern THICKNESS_PATTERN = Pattern.compile(
        "^(\\d+(?:\\.\\d{1,2})?)\\s*(mm|毫米|厘米|cm)?$", Pattern.CASE_INSENSITIVE
    );

    private static final Pattern QUANTITY_PATTERN = Pattern.compile(
        "^(\\d+(?:\\.\\d{1,3})?)\\s*([\\u4e00-\\u9fa5a-zA-Z]+)?$"
    );

    private static final double MIN_THICKNESS_MM = 0.3;
    private static final double MAX_THICKNESS_MM = 8.0;

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 30;
    private static final int MAX_SPEC_LENGTH = 50;
    private static final int MAX_QUANTITY_LENGTH = 30;

    public ValidationResult validateMaterialSummary(String materialSummaryJson) {
        ValidationResult result = new ValidationResult();

        if (materialSummaryJson == null || materialSummaryJson.trim().isEmpty()) {
            result.addWarning("材料信息为空，建议填写主材信息以便统计展示");
            return result;
        }

        try {
            JsonNode root = objectMapper.readTree(materialSummaryJson);
            validateMaterialSection(root.path("mainMaterials"), "主材", true, result);
            validateMaterialSection(root.path("auxMaterials"), "辅材", false, result);
            validateMaterialSection(root.path("tools"), "工具", false, result);

            int totalCount = 0;
            if (root.has("mainMaterials") && root.path("mainMaterials").isArray()) {
                totalCount += root.path("mainMaterials").size();
            }
            if (root.has("auxMaterials") && root.path("auxMaterials").isArray()) {
                totalCount += root.path("auxMaterials").size();
            }
            if (root.has("tools") && root.path("tools").isArray()) {
                totalCount += root.path("tools").size();
            }
            if (totalCount == 0) {
                result.addWarning("材料列表为空，建议至少填写一项主材");
            }

        } catch (Exception e) {
            result.addError("材料数据格式错误：" + e.getMessage());
        }

        return result;
    }

    private void validateMaterialSection(JsonNode items, String sectionName, boolean required, ValidationResult result) {
        if (items == null || items.isMissingNode() || !items.isArray()) {
            if (required) {
                result.addWarning("缺少" + sectionName + "信息");
            }
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            JsonNode item = items.get(i);
            String prefix = sectionName + "第" + (i + 1) + "项";
            validateSingleMaterial(item, prefix, result);
        }
    }

    public ValidationResult validateSingleMaterial(JsonNode item, String prefix, ValidationResult result) {
        if (item == null || item.isMissingNode()) {
            result.addError(prefix + "：数据缺失");
            return result;
        }

        String name = item.path("name").asText("").trim();
        String spec = item.path("spec").asText("").trim();
        String quantity = item.path("quantity").asText("").trim();

        validateName(name, prefix, result);
        validateSpec(spec, name, prefix, result);
        validateQuantity(quantity, prefix, result);

        return result;
    }

    public ValidationResult validateSingleMaterialMap(Map<String, String> material, String prefix) {
        ValidationResult result = new ValidationResult();
        if (material == null) {
            result.addError(prefix + "：数据缺失");
            return result;
        }
        String name = (material.get("name") != null) ? material.get("name").trim() : "";
        String spec = (material.get("spec") != null) ? material.get("spec").trim() : "";
        String quantity = (material.get("quantity") != null) ? material.get("quantity").trim() : "";

        validateName(name, prefix, result);
        validateSpec(spec, name, prefix, result);
        validateQuantity(quantity, prefix, result);
        return result;
    }

    private void validateName(String name, String prefix, ValidationResult result) {
        if (name.isEmpty()) {
            result.addError(prefix + "：名称不能为空");
            return;
        }
        if (name.length() < MIN_NAME_LENGTH) {
            result.addError(prefix + "：名称过短");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            result.addError(prefix + "：名称过长（最多" + MAX_NAME_LENGTH + "字）");
        }
        if (name.matches("^[\\s\\p{Punct}]+$")) {
            result.addError(prefix + "：名称不能仅包含符号或空格");
        }
        if (isVagueName(name)) {
            result.addWarning(prefix + "：名称\"" + name + "\"过于空泛，建议使用具体名称如\"植鞣革\"、\"疯马皮\"等");
        }
    }

    private boolean isVagueName(String name) {
        Set<String> vagueNames = new HashSet<>(Arrays.asList(
            "皮料", "皮", "材料", "物料", "料子", "主材", "辅材", "工具",
            "皮子", "皮革", "皮张", "皮件", "配件", "五金", "线", "绳子"
        ));
        String lower = name.toLowerCase().trim();
        if (vagueNames.contains(lower)) {
            return true;
        }
        if (lower.length() <= 2 && lower.contains("皮") && !VALID_LEATHER_TYPES.contains(lower)) {
            return true;
        }
        return false;
    }

    private void validateSpec(String spec, String materialName, String prefix, ValidationResult result) {
        if (spec.isEmpty()) {
            return;
        }
        if (spec.length() > MAX_SPEC_LENGTH) {
            result.addError(prefix + "：规格过长（最多" + MAX_SPEC_LENGTH + "字）");
        }

        boolean isLeather = isLikelyLeather(materialName);
        if (isLeather) {
            validateLeatherSpec(spec, prefix, result);
        }
    }

    private boolean isLikelyLeather(String name) {
        if (name == null || name.isEmpty()) return false;
        for (String type : VALID_LEATHER_TYPES) {
            if (name.contains(type)) {
                return true;
            }
        }
        return name.contains("皮") || name.contains("革");
    }

    private void validateLeatherSpec(String spec, String prefix, ValidationResult result) {
        boolean hasThickness = false;
        boolean hasColor = false;
        boolean hasValidLeatherType = false;

        String lowerSpec = spec.toLowerCase();

        for (String type : VALID_LEATHER_TYPES) {
            if (lowerSpec.contains(type.toLowerCase())) {
                hasValidLeatherType = true;
                break;
            }
        }

        Matcher thicknessMatcher = THICKNESS_PATTERN.matcher(spec);
        if (thicknessMatcher.find()) {
            hasThickness = true;
            try {
                double thickness = Double.parseDouble(thicknessMatcher.group(1));
                String unit = thicknessMatcher.group(2);
                if (unit != null && (unit.equals("厘米") || unit.equalsIgnoreCase("cm"))) {
                    thickness = thickness * 10;
                }
                if (thickness < MIN_THICKNESS_MM) {
                    result.addWarning(prefix + "：厚度" + thickness + "mm 偏小，常规皮料厚度在0.5mm-6mm之间");
                } else if (thickness > MAX_THICKNESS_MM) {
                    result.addWarning(prefix + "：厚度" + thickness + "mm 偏大，常规皮料厚度在0.5mm-6mm之间");
                }
            } catch (NumberFormatException ignored) {
            }
        }

        for (String color : VALID_COLORS) {
            if (spec.contains(color)) {
                hasColor = true;
                break;
            }
        }

        if (!hasThickness && !hasColor && !hasValidLeatherType) {
            result.addWarning(prefix + "：皮料规格建议包含厚度（如2.0mm）、颜色（如深棕）或皮种信息，便于展示统计");
        }
    }

    private void validateQuantity(String quantity, String prefix, ValidationResult result) {
        if (quantity.isEmpty()) {
            return;
        }
        if (quantity.length() > MAX_QUANTITY_LENGTH) {
            result.addError(prefix + "：用量过长（最多" + MAX_QUANTITY_LENGTH + "字）");
            return;
        }

        Matcher qtyMatcher = QUANTITY_PATTERN.matcher(quantity);
        if (!qtyMatcher.matches()) {
            result.addWarning(prefix + "：用量格式建议为\"数字+单位\"，如\"1张\"、\"0.5米\"");
            return;
        }

        try {
            double qtyValue = Double.parseDouble(qtyMatcher.group(1));
            if (qtyValue <= 0) {
                result.addError(prefix + "：用量必须大于0");
            }
        } catch (NumberFormatException ignored) {
        }

        String unit = qtyMatcher.group(2);
        if (unit != null && !unit.isEmpty()) {
            boolean validUnit = false;
            for (String validUnitItem : VALID_QUANTITY_UNITS) {
                if (unit.equalsIgnoreCase(validUnitItem)) {
                    validUnit = true;
                    break;
                }
            }
            if (!validUnit) {
                result.addWarning(prefix + "：单位\"" + unit + "\"不在常见单位列表中，建议使用：张、片、米、英尺、个、套等");
            }
        }
    }

    public ValidationResult validateMaterialsText(String materialsText) {
        ValidationResult result = new ValidationResult();
        if (materialsText == null || materialsText.trim().isEmpty()) {
            return result;
        }

        String trimmed = materialsText.trim();
        if (trimmed.length() > 500) {
            result.addWarning("用料描述过长（超过500字），建议使用结构化材料信息");
        }

        if (trimmed.length() < 2) {
            result.addWarning("用料描述过短，建议使用结构化材料信息以获得更好的展示效果");
        }

        return result;
    }

    public Map<String, Object> getValidationReferenceData() {
        Map<String, Object> data = new HashMap<>();
        data.put("leatherTypes", new ArrayList<>(VALID_LEATHER_TYPES));
        data.put("colors", new ArrayList<>(VALID_COLORS));
        data.put("quantityUnits", new ArrayList<>(VALID_QUANTITY_UNITS));
        data.put("thicknessRange", Map.of(
            "min", MIN_THICKNESS_MM,
            "max", MAX_THICKNESS_MM,
            "unit", "mm"
        ));
        data.put("fieldLimits", Map.of(
            "nameMaxLength", MAX_NAME_LENGTH,
            "specMaxLength", MAX_SPEC_LENGTH,
            "quantityMaxLength", MAX_QUANTITY_LENGTH
        ));
        return data;
    }
}
