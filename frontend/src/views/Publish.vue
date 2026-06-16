<template>
  <div class="publish-page">
    <van-nav-bar title="发布作品" left-arrow @click-left="handleBack" fixed>
      <template #right>
        <van-button type="primary" size="small" @click="submit" :loading="submitting" v-if="currentStep === 3">
          发布
        </van-button>
      </template>
    </van-nav-bar>

    <div class="publish-content">
      <div class="step-indicator">
        <div
          v-for="(step, idx) in steps"
          :key="idx"
          class="step-item"
          :class="{ active: currentStep === idx, done: currentStep > idx }"
          @click="goToStep(idx)"
        >
          <div class="step-circle">{{ idx + 1 }}</div>
          <div class="step-label">{{ step.label }}</div>
        </div>
        <div class="step-line" :style="{ width: stepLineWidth }"></div>
      </div>

      <van-form class="step-form">
        <div v-show="currentStep === 0" class="step-panel">
          <van-cell-group inset title="作品标题">
            <van-field
              v-model="form.title"
              label="标题"
              placeholder="请输入作品标题"
              maxlength="50"
              show-word-limit
            />
          </van-cell-group>

          <van-cell-group inset title="作品简介">
            <van-field
              v-model="form.content"
              label="简介"
              type="textarea"
              placeholder="简单介绍一下你的作品..."
              rows="3"
              maxlength="500"
              show-word-limit
            />
          </van-cell-group>

          <van-cell-group inset title="分类信息">
            <van-field
              :model-value="selectedCategoryName"
              is-link
              readonly
              label="皮具品类"
              placeholder="请选择品类"
              @click="openCategoryPicker"
            />
            <van-field
              :model-value="selectedCraftName"
              is-link
              readonly
              label="工艺类型"
              placeholder="请选择工艺"
              @click="openCraftPicker"
            />
          </van-cell-group>
        </div>

        <div v-show="currentStep === 1" class="step-panel">
          <div v-if="materialValidation.warnings.length || !materialValidation.valid" class="mat-validation-panel">
            <div v-if="!materialValidation.valid" class="mat-errors">
              <div class="mat-val-title">❌ 请修正以下问题</div>
              <div v-for="(e, ei) in materialValidation.errors" :key="'e'+ei" class="mat-val-item error">{{ e }}</div>
            </div>
            <div v-if="materialValidation.warnings.length" class="mat-warns">
              <div class="mat-val-title">💡 优化建议</div>
              <div v-for="(w, wi) in materialValidation.warnings" :key="'w'+wi" class="mat-val-item warning">{{ w }}</div>
            </div>
          </div>

          <van-cell-group inset title="材料用量速览">
            <div class="material-editor">
              <div class="mat-section" v-for="section in materialSections" :key="section.key">
                <div class="mat-section-header">
                  <span class="mat-icon">{{ section.icon }}</span>
                  <span class="mat-title">{{ section.label }}</span>
                  <van-button
                    size="mini"
                    type="primary"
                    plain
                    icon="plus"
                    @click="addMaterialItem(section.key)"
                  >
                    添加
                  </van-button>
                </div>
                <div
                  class="mat-item"
                  v-for="(item, idx) in form.materialSummaryObj[section.key]"
                  :key="idx"
                >
                  <div class="mat-field-group">
                    <div class="mat-label-row">
                      <span class="mat-field-label">名称</span>
                      <van-button
                        v-if="section.key === 'mainMaterials'"
                        size="mini"
                        plain
                        type="primary"
                        class="quick-btn"
                        @click="openQuickPick('leatherType', section.key, idx)"
                      >
                        选皮种
                      </van-button>
                    </div>
                    <van-field
                      v-model="item.name"
                      placeholder="如：植鞣革"
                      class="mat-input"
                      :error="getItemValidation(section.key, idx).errors.some(e => e.includes('名称'))"
                    />
                  </div>

                  <div class="mat-field-group">
                    <div class="mat-label-row">
                      <span class="mat-field-label">规格</span>
                      <div class="quick-btns">
                        <van-button
                          size="mini"
                          plain
                          type="primary"
                          class="quick-btn"
                          @click="openQuickPick('thickness', section.key, idx)"
                        >
                          厚度
                        </van-button>
                        <van-button
                          size="mini"
                          plain
                          type="primary"
                          class="quick-btn"
                          @click="openQuickPick('color', section.key, idx)"
                        >
                          颜色
                        </van-button>
                      </div>
                    </div>
                    <van-field
                      v-model="item.spec"
                      placeholder="如：2.0mm 深棕"
                      class="mat-input"
                    />
                  </div>

                  <div class="mat-field-group">
                    <div class="mat-label-row">
                      <span class="mat-field-label">用量</span>
                    </div>
                    <van-field
                      v-model="item.quantity"
                      placeholder="如：1张"
                      class="mat-input"
                    />
                  </div>

                  <div class="mat-item-footer">
                    <div class="mat-warnings" v-if="getItemValidation(section.key, idx).warnings.length">
                      <span
                        v-for="(w, wi) in getItemValidation(section.key, idx).warnings"
                        :key="wi"
                        class="mat-warning"
                      >
                        ⚠️ {{ w }}
                      </span>
                    </div>
                    <van-icon
                      name="delete-o"
                      class="mat-delete"
                      @click="removeMaterialItem(section.key, idx)"
                    />
                  </div>
                </div>
                <div class="mat-empty" v-if="!form.materialSummaryObj[section.key].length">
                  暂无{{ section.label }}，点击上方添加
                </div>
              </div>
            </div>
          </van-cell-group>

          <van-cell-group inset title="用料描述（可选）">
            <van-field
              v-model="form.materials"
              label="用料"
              type="textarea"
              placeholder="使用了哪些皮料、五金、线等..."
              rows="2"
            />
          </van-cell-group>
        </div>

        <div v-show="currentStep === 2" class="step-panel">
          <van-cell-group inset title="工序步骤（分镜）">
            <div class="steps-editor">
              <div
                v-for="(step, idx) in form.steps"
                :key="idx"
                class="step-editor-item"
              >
                <div class="step-header">
                  <span class="step-index">第 {{ idx + 1 }} 步</span>
                  <van-icon
                    name="delete-o"
                    class="delete-btn"
                    @click="removeStep(idx)"
                  />
                </div>

                <van-field
                  v-model="step.stepName"
                  label="步骤名称"
                  placeholder="如：裁下料片"
                  maxlength="30"
                />

                <van-field
                  :model-value="getStepTypeName(step.stepType)"
                  is-link
                  readonly
                  label="步骤类型"
                  placeholder="选择类型"
                  @click="openStepTypePicker(idx)"
                />

                <van-field
                  v-model="step.timeCost"
                  type="digit"
                  label="耗时"
                  placeholder="预计耗时"
                  :suffix="'分钟'"
                  maxlength="5"
                />

                <van-field
                  v-model="step.materials"
                  label="本步材料"
                  type="textarea"
                  placeholder="如：植鞣革2.0mm，裁皮刀"
                  rows="1"
                  maxlength="200"
                />

                <van-field
                  v-model="step.description"
                  label="操作说明"
                  type="textarea"
                  placeholder="详细描述本步骤的操作过程..."
                  rows="2"
                  maxlength="500"
                />

                <van-field
                  v-model="step.tips"
                  label="注意要点"
                  type="textarea"
                  placeholder="本步骤需要注意的技巧和坑点..."
                  rows="2"
                  maxlength="300"
                />

                <div class="step-images-upload">
                  <div class="upload-label">本步过程图</div>
                  <van-uploader
                    v-model="step._fileList"
                    multiple
                    :max-count="5"
                    :after-read="(file) => afterStepRead(file, idx)"
                  />
                </div>

                <div v-if="idx < form.steps.length - 1" class="step-divider"></div>
              </div>

              <van-button
                block
                plain
                type="primary"
                icon="plus"
                @click="addStep"
                class="add-step-btn"
              >
                添加工序步骤
              </van-button>
            </div>
          </van-cell-group>

          <van-cell-group inset title="旧版制作心得（可选）">
            <van-field
              v-model="form.craftSteps"
              label="制作心得"
              type="textarea"
              placeholder="可选：简单的文本形式制作流程，或使用上面的结构化步骤..."
              rows="4"
            />
          </van-cell-group>
        </div>

        <div v-show="currentStep === 3" class="step-panel">
          <van-cell-group inset title="成品图片">
            <van-uploader
              v-model="fileList"
              multiple
              :max-count="9"
              :after-read="afterRead"
            />
            <div class="upload-hint">第一张图片将作为作品封面</div>
          </van-cell-group>

          <van-cell-group inset title="过程图（可选，已绑定到步骤的可不传）">
            <van-uploader
              v-model="processFileList"
              multiple
              :max-count="20"
              :after-read="afterProcessRead"
            />
          </van-cell-group>
        </div>
      </van-form>

      <div class="step-nav">
        <van-button
          block
          type="default"
          :disabled="currentStep === 0"
          @click="prevStep"
          class="nav-btn prev-btn"
        >
          上一步
        </van-button>
        <van-button
          block
          type="primary"
          @click="nextStep"
          class="nav-btn next-btn"
          v-if="currentStep < 3"
        >
          下一步
        </van-button>
        <van-button
          block
          type="primary"
          @click="submit"
          :loading="submitting"
          class="nav-btn submit-btn"
          v-else
        >
          发布作品
        </van-button>
      </div>
    </div>

    <van-popup v-model:show="showCategoryPicker" position="bottom" round>
      <div class="option-panel">
        <div class="option-toolbar">
          <button type="button" class="option-action cancel" @click="showCategoryPicker = false">取消</button>
          <div class="option-title">选择皮具品类</div>
          <button type="button" class="option-action confirm" @click="confirmCategorySelection">确认</button>
        </div>
        <div class="option-list">
          <button
            v-for="option in categories"
            :key="option.value"
            type="button"
            class="option-item"
            :class="{ active: categoryPickerValue[0] === option.value }"
            @click="selectCategoryOption(option)"
          >
            {{ option.text }}
          </button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showCraftPicker" position="bottom" round>
      <div class="option-panel">
        <div class="option-toolbar">
          <button type="button" class="option-action cancel" @click="showCraftPicker = false">取消</button>
          <div class="option-title">选择工艺类型</div>
          <button type="button" class="option-action confirm" @click="confirmCraftSelection">确认</button>
        </div>
        <div class="option-list">
          <button
            v-for="option in craftTypes"
            :key="option.value"
            type="button"
            class="option-item"
            :class="{ active: craftPickerValue[0] === option.value }"
            @click="selectCraftOption(option)"
          >
            {{ option.text }}
          </button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showStepTypePicker" position="bottom" round>
      <div class="option-panel">
        <div class="option-toolbar">
          <button type="button" class="option-action cancel" @click="showStepTypePicker = false">取消</button>
          <div class="option-title">选择步骤类型</div>
          <button type="button" class="option-action confirm" @click="confirmStepTypeSelection">确认</button>
        </div>
        <div class="option-list">
          <button
            v-for="option in stepTypeOptions"
            :key="option.value"
            type="button"
            class="option-item"
            :class="{ active: stepTypePickerValue[0] === option.value }"
            @click="selectStepTypeOption(option)"
          >
            {{ option.text }}
          </button>
        </div>
      </div>
    </van-popup>

    <van-popup v-model:show="showQuickPick" position="bottom" round>
      <div class="option-panel">
        <div class="option-toolbar">
          <button type="button" class="option-action cancel" @click="showQuickPick = false">取消</button>
          <div class="option-title">
            {{ quickPickType === 'thickness' ? '选择厚度' : quickPickType === 'leatherType' ? '选择皮种' : '选择颜色' }}
          </div>
          <button type="button" class="option-action confirm" @click="confirmQuickPick">确认</button>
        </div>
        <div class="option-list">
          <template v-if="quickPickType === 'thickness'">
            <button
              v-for="option in THICKNESS_OPTIONS"
              :key="option.value"
              type="button"
              class="option-item"
              :class="{ active: thicknessPickerValue[0] === option.value }"
              @click="thicknessPickerValue = [option.value]"
            >
              {{ option.label }}
            </button>
          </template>
          <template v-else-if="quickPickType === 'leatherType'">
            <button
              v-for="option in LEATHER_TYPE_OPTIONS"
              :key="option.value"
              type="button"
              class="option-item"
              :class="{ active: leatherTypePickerValue[0] === option.value }"
              @click="leatherTypePickerValue = [option.value]"
            >
              {{ option.label }}
            </button>
          </template>
          <template v-else-if="quickPickType === 'color'">
            <button
              v-for="option in COLOR_OPTIONS"
              :key="option.value"
              type="button"
              class="option-item"
              :class="{ active: colorPickerValue[0] === option.value }"
              @click="colorPickerValue = [option.value]"
            >
              {{ option.label }}
            </button>
          </template>
        </div>
      </div>
    </van-popup>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, publishWork } from '@/api'
import { useUserStore } from '@/store/user'
import { showToast, showDialog } from 'vant'
import {
  validateAllMaterials,
  validateSingleMaterial,
  isLikelyLeather,
  isVagueName,
  THICKNESS_OPTIONS,
  LEATHER_TYPE_OPTIONS,
  COLOR_OPTIONS
} from '@/utils/materialValidator'

const router = useRouter()
const userStore = useUserStore()

const stepTypeMap = {
  cutting: { name: '裁切', icon: '✂️' },
  sewing: { name: '缝制', icon: '🧵' },
  edge: { name: '封边', icon: '🎨' },
  hardware: { name: '五金安装', icon: '🔩' },
  shaping: { name: '塑形', icon: '✨' },
  carving: { name: '皮雕', icon: '🗡️' },
  other: { name: '其他', icon: '📝' }
}

const materialSections = [
  { key: 'mainMaterials', label: '主材', icon: '🐂' },
  { key: 'auxMaterials', label: '辅材', icon: '🔩' },
  { key: 'tools', label: '工具', icon: '🛠️' }
]

const steps = [
  { label: '基础信息' },
  { label: '材料' },
  { label: '步骤' },
  { label: '图片' }
]

const createEmptyMaterial = () => ({
  name: '',
  spec: '',
  quantity: ''
})

const getStepTypeName = (type) => stepTypeMap[type]?.name || '请选择'

const createEmptyStep = () => ({
  stepName: '',
  stepType: '',
  timeCost: null,
  materials: '',
  tips: '',
  description: '',
  sort: 0,
  images: [],
  _fileList: []
})

const currentStep = ref(0)

const stepLineWidth = computed(() => {
  const progress = currentStep.value / (steps.length - 1) * 100
  return `calc(${progress}% - 1px)`
})

const form = ref({
  title: '',
  content: '',
  materials: '',
  materialSummary: '',
  craftSteps: '',
  categoryId: null,
  craftTypeId: null,
  steps: [createEmptyStep()],
  materialSummaryObj: {
    mainMaterials: [],
    auxMaterials: [],
    tools: []
  }
})

const fileList = ref([])
const processFileList = ref([])
const submitting = ref(false)
const showCategoryPicker = ref(false)
const showCraftPicker = ref(false)
const showStepTypePicker = ref(false)
const currentStepIndex = ref(-1)
const categories = ref([])
const craftTypes = ref([])
const selectedCategoryName = ref('')
const selectedCraftName = ref('')
const categoryPickerValue = ref([])
const craftPickerValue = ref([])
const stepTypePickerValue = ref([])

const materialValidation = ref({ errors: [], warnings: [], valid: true })
const showQuickPick = ref(false)
const quickPickType = ref('')
const quickPickSection = ref('')
const quickPickIndex = ref(-1)
const thicknessPickerValue = ref([])
const leatherTypePickerValue = ref([])
const colorPickerValue = ref([])

const stepTypeOptions = computed(() =>
  Object.entries(stepTypeMap).map(([key, val]) => ({
    text: `${val.icon} ${val.name}`,
    value: key
  }))
)

const goToStep = (idx) => {
  if (idx < 0 || idx >= steps.length) return
  currentStep.value = idx
}

const prevStep = () => {
  if (currentStep.value > 0) {
    currentStep.value--
  }
}

const nextStep = () => {
  if (validateCurrentStep()) {
    currentStep.value++
  }
}

const validateCurrentStep = () => {
  switch (currentStep.value) {
    case 0:
      if (!form.value.title || !form.value.title.trim()) {
        showToast('请输入作品标题')
        return false
      }
      if (!form.value.categoryId) {
        showToast('请选择皮具品类')
        return false
      }
      return true
    case 1:
      runMaterialValidation()
      if (!materialValidation.value.valid) {
        showToast(materialValidation.value.errors[0])
        return false
      }
      return true
    case 2:
      return true
    case 3:
      if (!fileList.value.length) {
        showToast('请上传成品图片')
        return false
      }
      return true
    default:
      return true
  }
}

const runMaterialValidation = () => {
  materialValidation.value = validateAllMaterials(
    form.value.materialSummaryObj,
    form.value.materials
  )
}

const getItemValidation = (sectionKey, index) => {
  const sectionLabel = sectionKey === 'mainMaterials' ? '主材' :
    sectionKey === 'auxMaterials' ? '辅材' : '工具'
  const prefix = `${sectionLabel}第${index + 1}项：`
  const item = form.value.materialSummaryObj[sectionKey]?.[index]
  if (!item) return { errors: [], warnings: [] }
  const result = validateSingleMaterial(item, prefix)
  return { errors: result.errors, warnings: result.warnings }
}

const openQuickPick = (type, sectionKey, index) => {
  quickPickType.value = type
  quickPickSection.value = sectionKey
  quickPickIndex.value = index

  if (type === 'thickness') {
    const current = form.value.materialSummaryObj[sectionKey][index].spec || ''
    const match = THICKNESS_OPTIONS.find(o => current.includes(o.value))
    thicknessPickerValue.value = match ? [match.value] : [THICKNESS_OPTIONS[5].value]
  } else if (type === 'leatherType') {
    const current = form.value.materialSummaryObj[sectionKey][index].name || ''
    const match = LEATHER_TYPE_OPTIONS.find(o => current.includes(o.value))
    leatherTypePickerValue.value = match ? [match.value] : []
  } else if (type === 'color') {
    const current = form.value.materialSummaryObj[sectionKey][index].spec || ''
    const match = COLOR_OPTIONS.find(o => current.includes(o.value))
    colorPickerValue.value = match ? [match.value] : []
  }

  showQuickPick.value = true
}

const confirmQuickPick = () => {
  const sectionKey = quickPickSection.value
  const index = quickPickIndex.value
  if (index < 0 || !form.value.materialSummaryObj[sectionKey]?.[index]) {
    showQuickPick.value = false
    return
  }
  const item = form.value.materialSummaryObj[sectionKey][index]

  if (quickPickType.value === 'thickness') {
    const thickness = thicknessPickerValue.value[0] || ''
    if (thickness) {
      let spec = item.spec || ''
      spec = spec.replace(/\d+(?:\.\d{1,2})?\s*(mm|毫米|厘米|cm)?/gi, '').trim()
      item.spec = spec ? `${thickness} ${spec}` : thickness
    }
  } else if (quickPickType.value === 'leatherType') {
    const leatherType = leatherTypePickerValue.value[0] || ''
    if (leatherType) {
      item.name = leatherType
    }
  } else if (quickPickType.value === 'color') {
    const color = colorPickerValue.value[0] || ''
    if (color) {
      let spec = item.spec || ''
      spec = spec.replace(
        /原色|自然色|本色|黑色|白色|红色|黄色|蓝色|绿色|紫色|橙色|粉色|灰色|棕色|褐色|深棕|浅棕|红棕|黄棕|咖啡|咖啡色|巧克力|巧克力色|深红|酒红|枣红|砖红|正红|中国红|深蓝|藏蓝|宝蓝|天蓝|湖蓝|孔雀蓝|深绿|墨绿|草绿|军绿|橄榄绿|薄荷绿|深黄|浅黄|米黄|杏黄|鹅黄|姜黄|柠檬黄|深灰|浅灰|银灰|炭灰|米白|乳白|象牙白|珍珠白|雪白|驼色|米色|卡其|卡其色|杏色|藕色|肉色|金色|银色|古铜色|青铜色|玫瑰金/g,
        ''
      ).trim()
      item.spec = spec ? `${color} ${spec}` : color
    }
  }

  showQuickPick.value = false
  runMaterialValidation()
}

const handleBack = () => {
  const hasContent = form.value.title || form.value.content || fileList.value.length > 0 ||
    form.value.steps.some(s => s.stepName) ||
    Object.values(form.value.materialSummaryObj).some(arr => arr.length > 0)

  if (hasContent) {
    showDialog({
      title: '确认离开',
      message: '当前填写的内容将会丢失，确定要离开吗？',
      showCancelButton: true
    }).then(() => {
      router.back()
    }).catch(() => {})
  } else {
    router.back()
  }
}

const addStep = () => {
  form.value.steps.push(createEmptyStep())
}

const removeStep = (idx) => {
  if (form.value.steps.length <= 1) {
    showToast('至少保留一个步骤')
    return
  }
  form.value.steps.splice(idx, 1)
}

const addMaterialItem = (sectionKey) => {
  form.value.materialSummaryObj[sectionKey].push(createEmptyMaterial())
}

const removeMaterialItem = (sectionKey, idx) => {
  form.value.materialSummaryObj[sectionKey].splice(idx, 1)
}

const openCategoryPicker = () => {
  if (!categoryPickerValue.value.length && categories.value.length) {
    categoryPickerValue.value = [categories.value[0].value]
  }
  showCategoryPicker.value = true
}

const selectCategoryOption = (option) => {
  categoryPickerValue.value = [option.value]
}

const confirmCategorySelection = () => {
  const option = categories.value.find(item => item.value === categoryPickerValue.value[0]) || categories.value[0]
  if (option) {
    form.value.categoryId = option.value
    selectedCategoryName.value = option.text
    categoryPickerValue.value = [option.value]
  }
  showCategoryPicker.value = false
}

const openCraftPicker = () => {
  if (!craftPickerValue.value.length && craftTypes.value.length) {
    craftPickerValue.value = [craftTypes.value[0].value]
  }
  showCraftPicker.value = true
}

const selectCraftOption = (option) => {
  craftPickerValue.value = [option.value]
}

const confirmCraftSelection = () => {
  const option = craftTypes.value.find(item => item.value === craftPickerValue.value[0]) || craftTypes.value[0]
  if (option) {
    form.value.craftTypeId = option.value
    selectedCraftName.value = option.text
    craftPickerValue.value = [option.value]
  }
  showCraftPicker.value = false
}

const openStepTypePicker = (idx) => {
  currentStepIndex.value = idx
  const currentType = form.value.steps[idx]?.stepType
  stepTypePickerValue.value = currentType ? [currentType] : [stepTypeOptions.value[0]?.value]
  showStepTypePicker.value = true
}

const selectStepTypeOption = (option) => {
  stepTypePickerValue.value = [option.value]
}

const confirmStepTypeSelection = () => {
  const option = stepTypeOptions.value.find(item => item.value === stepTypePickerValue.value[0]) || stepTypeOptions.value[0]
  if (currentStepIndex.value >= 0 && option) {
    form.value.steps[currentStepIndex.value].stepType = option.value
    stepTypePickerValue.value = [option.value]
  }
  showStepTypePicker.value = false
}

const afterRead = (file) => {
  fileList.value = fileList.value.concat(file)
  form.value.cover = file[0]?.content || ''
}

const afterProcessRead = (file) => {
  processFileList.value = processFileList.value.concat(file)
}

const afterStepRead = (file, stepIdx) => {
  if (!form.value.steps[stepIdx]._fileList) {
    form.value.steps[stepIdx]._fileList = []
  }
  form.value.steps[stepIdx]._fileList = form.value.steps[stepIdx]._fileList.concat(file)
}

const submit = async () => {
  if (!userStore.isLoggedIn()) {
    showToast('请先登录')
    router.push('/login')
    return
  }

  if (!form.value.title) {
    showToast('请输入标题')
    currentStep.value = 0
    return
  }
  if (!form.value.categoryId) {
    showToast('请选择品类')
    currentStep.value = 0
    return
  }

  runMaterialValidation()
  if (!materialValidation.value.valid) {
    showToast(materialValidation.value.errors[0])
    currentStep.value = 1
    return
  }

  if (!fileList.value.length) {
    showToast('请上传成品图片')
    currentStep.value = 3
    return
  }

  submitting.value = true
  try {
    const images = fileList.value.map(f => f.content || f.url || 'https://picsum.photos/400/300')
    const processImages = processFileList.value.map(f => f.content || f.url || 'https://picsum.photos/400/300')

    const steps = form.value.steps
      .filter(s => s.stepName && s.stepName.trim())
      .map((s, idx) => ({
        stepName: s.stepName.trim(),
        stepType: s.stepType,
        timeCost: s.timeCost ? Number(s.timeCost) : null,
        materials: s.materials,
        tips: s.tips,
        description: s.description,
        sort: idx,
        images: (s._fileList || []).map(f => f.content || f.url || 'https://picsum.photos/400/300')
      }))

    const materialSummaryData = {}
    for (const section of materialSections) {
      const items = form.value.materialSummaryObj[section.key]
        .filter(item => item.name && item.name.trim())
        .map(item => ({
          name: item.name.trim(),
          spec: item.spec?.trim() || '',
          quantity: item.quantity?.trim() || ''
        }))
      if (items.length) {
        materialSummaryData[section.key] = items
      }
    }
    const hasMaterialSummary = Object.keys(materialSummaryData).length > 0

    await publishWork({
      ...form.value,
      cover: images[0],
      images,
      processImages,
      steps: steps.length ? steps : undefined,
      materialSummary: hasMaterialSummary ? JSON.stringify(materialSummaryData) : undefined
    }, userStore.userInfo.id)

    showToast('发布成功')
    setTimeout(() => {
      router.push('/')
    }, 1500)
  } catch (e) {
    // error handled by interceptor
  } finally {
    submitting.value = false
  }
}

watch(
  () => [form.value.materialSummaryObj, form.value.materials, currentStep.value],
  () => {
    if (currentStep.value === 1) {
      runMaterialValidation()
    }
  },
  { deep: true }
)

watch(currentStep, (newStep) => {
  if (newStep === 1) {
    runMaterialValidation()
  }
})

onMounted(async () => {
  const [cats, crafts] = await Promise.all([
    getCategories('category'),
    getCategories('craft')
  ])
  categories.value = cats.map(c => ({ text: c.name, value: c.id }))
  craftTypes.value = crafts.map(c => ({ text: c.name, value: c.id }))
})
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  padding-bottom: 80px;
  background: #f5f5f5;
}

.publish-content {
  padding-top: 46px;
}

.step-indicator {
  position: relative;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  padding: 16px 24px 12px;
  background: #fff;
  margin-bottom: 12px;
}

.step-indicator::before {
  content: '';
  position: absolute;
  top: 28px;
  left: 24px;
  right: 24px;
  height: 2px;
  background: #ebedf0;
  z-index: 0;
}

.step-line {
  position: absolute;
  top: 28px;
  left: 24px;
  height: 2px;
  background: linear-gradient(90deg, #c08457, #8b5a2b);
  z-index: 1;
  transition: width 0.3s ease;
}

.step-item {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  flex: 1;
}

.step-circle {
  width: 26px;
  height: 26px;
  border-radius: 50%;
  background: #fff;
  border: 2px solid #dcdee0;
  color: #969799;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
}

.step-item.done .step-circle {
  background: linear-gradient(135deg, #c08457, #8b5a2b);
  border-color: #8b5a2b;
  color: #fff;
}

.step-item.active .step-circle {
  background: linear-gradient(135deg, #d4a574, #c08457);
  border-color: #c08457;
  color: #fff;
  transform: scale(1.1);
  box-shadow: 0 2px 8px rgba(139, 90, 43, 0.3);
}

.step-label {
  margin-top: 6px;
  font-size: 12px;
  color: #969799;
  white-space: nowrap;
}

.step-item.active .step-label {
  color: #8b5a2b;
  font-weight: 600;
}

.step-item.done .step-label {
  color: #c08457;
}

.step-form {
  padding-bottom: 12px;
}

.step-panel {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(8px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.step-nav {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  display: flex;
  gap: 10px;
  padding: 10px 16px;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.06);
  z-index: 100;
}

.nav-btn {
  flex: 1;
}

.prev-btn {
  flex: 0 0 35%;
}

.next-btn,
.submit-btn {
  flex: 1;
}

.upload-hint {
  padding: 0 16px 12px;
  font-size: 12px;
  color: #969799;
}

.material-editor {
  padding: 12px;
}

.mat-section {
  background: #fff;
  border-radius: 10px;
  padding: 12px;
  margin-bottom: 12px;
  border: 1px solid #f0e8df;
}

.mat-section-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px dashed #f0e8df;
}

.mat-icon {
  font-size: 18px;
}

.mat-title {
  font-size: 15px;
  font-weight: 600;
  color: #8b5a2b;
  flex: 1;
}

.mat-item {
  display: flex;
  flex-direction: column;
  gap: 8px;
  margin-bottom: 10px;
  background: #faf7f3;
  border-radius: 8px;
  padding: 10px;
  border: 1px solid #f0e8df;
}

.mat-field-group {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mat-label-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 4px;
}

.mat-field-label {
  font-size: 12px;
  color: #8b5a2b;
  font-weight: 500;
}

.quick-btns {
  display: flex;
  gap: 6px;
}

.quick-btn {
  padding: 0 8px !important;
  height: 24px !important;
  font-size: 11px !important;
}

.mat-input :deep(.van-field__control) {
  font-size: 14px;
}

.mat-input :deep(.van-cell) {
  padding: 6px 8px;
  background: #fff;
  border-radius: 6px;
}

.mat-item-footer {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 8px;
}

.mat-warnings {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.mat-warning {
  font-size: 11px;
  color: #ff976a;
  background: #fff7e8;
  padding: 4px 8px;
  border-radius: 4px;
  line-height: 1.4;
}

.mat-delete {
  color: #ee0a24;
  font-size: 20px;
  padding: 4px;
  cursor: pointer;
  flex-shrink: 0;
}

.mat-validation-panel {
  margin: 0 16px 12px;
  border-radius: 10px;
  overflow: hidden;
}

.mat-errors, .mat-warns {
  padding: 10px 12px;
}

.mat-errors {
  background: #fef0f0;
  border: 1px solid #fbc4c4;
}

.mat-warns {
  background: #fff7e8;
  border: 1px solid #ffe2b8;
  border-top: none;
}

.mat-val-title {
  font-size: 13px;
  font-weight: 600;
  margin-bottom: 6px;
  color: #646566;
}

.mat-val-item {
  font-size: 12px;
  line-height: 1.6;
  padding: 2px 0;
}

.mat-val-item.error {
  color: #ee0a24;
}

.mat-val-item.warning {
  color: #ff976a;
}

.mat-empty {
  text-align: center;
  color: #ccc;
  font-size: 13px;
  padding: 16px 0;
}

.steps-editor {
  padding: 12px;
}

.step-editor-item {
  background: #fff;
  border-radius: 12px;
  padding: 12px;
  margin-bottom: 12px;
}

.step-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 8px;
  border-bottom: 1px solid #f0f0f0;
  margin-bottom: 8px;
}

.step-index {
  font-size: 15px;
  font-weight: 600;
  color: #8b5a2b;
}

.delete-btn {
  color: #ee0a24;
  font-size: 18px;
  cursor: pointer;
}

.step-images-upload {
  padding: 8px 12px;
}

.upload-label {
  font-size: 13px;
  color: #646566;
  margin-bottom: 8px;
}

.step-divider {
  height: 1px;
  background: #f0f0f0;
  margin: 12px 0;
}

.add-step-btn {
  margin-top: 8px;
}

.option-panel {
  background: #fff;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}

.option-toolbar {
  height: 44px;
  display: grid;
  grid-template-columns: 72px 1fr 72px;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.option-title {
  text-align: center;
  font-size: 16px;
  font-weight: 600;
  color: #323233;
}

.option-action {
  height: 44px;
  border: 0;
  background: transparent;
  font-size: 14px;
  cursor: pointer;
}

.option-action.cancel {
  color: #969799;
}

.option-action.confirm {
  color: #8b5a2b;
  font-weight: 600;
}

.option-list {
  overflow-y: auto;
  padding: 8px 16px 18px;
}

.option-item {
  width: 100%;
  min-height: 44px;
  border: 0;
  border-radius: 8px;
  background: #fff;
  color: #323233;
  font-size: 15px;
  text-align: center;
  cursor: pointer;
}

.option-item + .option-item {
  margin-top: 4px;
}

.option-item.active {
  background: #f5e6d3;
  color: #8b5a2b;
  font-weight: 600;
}
</style>
