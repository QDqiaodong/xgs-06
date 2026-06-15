<template>
  <div class="publish-page">
    <van-nav-bar title="发布作品" left-arrow @click-left="$router.back()" fixed>
      <template #right>
        <van-button type="primary" size="small" @click="submit" :loading="submitting">
          发布
        </van-button>
      </template>
    </van-nav-bar>

    <div class="publish-content">
      <van-form>
        <van-cell-group inset title="基础信息">
          <van-field
            v-model="form.title"
            label="标题"
            placeholder="请输入作品标题"
            maxlength="50"
            show-word-limit
          />
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

        <van-cell-group inset title="成品图片">
          <van-uploader
            v-model="fileList"
            multiple
            :max-count="9"
            :after-read="afterRead"
          />
        </van-cell-group>

        <van-cell-group inset title="分类信息">
          <van-field
            v-model="showCategoryPicker"
            is-link
            readonly
            label="皮具品类"
            :value="selectedCategoryName"
            placeholder="请选择品类"
            @click="showCategoryPicker = true"
          />
          <van-field
            v-model="showCraftPicker"
            is-link
            readonly
            label="工艺类型"
            :value="selectedCraftName"
            placeholder="请选择工艺"
            @click="showCraftPicker = true"
          />
        </van-cell-group>

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
                <van-field
                  v-model="item.name"
                  placeholder="名称"
                  class="mat-name-input"
                />
                <van-field
                  v-model="item.spec"
                  placeholder="规格"
                  class="mat-spec-input"
                />
                <van-field
                  v-model="item.quantity"
                  placeholder="用量"
                  class="mat-qty-input"
                />
                <van-icon
                  name="delete-o"
                  class="mat-delete"
                  @click="removeMaterialItem(section.key, idx)"
                />
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
                v-model="step.stepType"
                is-link
                readonly
                label="步骤类型"
                :value="getStepTypeName(step.stepType)"
                placeholder="选择类型"
                @click="openStepTypePicker(idx)"
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

        <van-cell-group inset title="过程图（可选，已绑定到步骤的可不传）">
          <van-uploader
            v-model="processFileList"
            multiple
            :max-count="20"
            :after-read="afterProcessRead"
          />
        </van-cell-group>
      </van-form>
    </div>

    <van-popup v-model:show="showCategoryPicker" position="bottom" round>
      <van-picker
        :columns="categories"
        show-toolbar
        title="选择皮具品类"
        @confirm="onCategoryConfirm"
        @cancel="showCategoryPicker = false"
      />
    </van-popup>

    <van-popup v-model:show="showCraftPicker" position="bottom" round>
      <van-picker
        :columns="craftTypes"
        show-toolbar
        title="选择工艺类型"
        @confirm="onCraftConfirm"
        @cancel="showCraftPicker = false"
      />
    </van-popup>

    <van-popup v-model:show="showStepTypePicker" position="bottom" round>
      <van-picker
        :columns="stepTypeOptions"
        show-toolbar
        title="选择步骤类型"
        @confirm="onStepTypeConfirm"
        @cancel="showStepTypePicker = false"
      />
    </van-popup>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { getCategories, publishWork } from '@/api'
import { useUserStore } from '@/store/user'
import { showToast } from 'vant'

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

const createEmptyMaterial = () => ({
  name: '',
  spec: '',
  quantity: ''
})

const getStepTypeName = (type) => stepTypeMap[type]?.name || '请选择'

const createEmptyStep = () => ({
  stepName: '',
  stepType: '',
  materials: '',
  tips: '',
  description: '',
  sort: 0,
  images: [],
  _fileList: []
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

const stepTypeOptions = computed(() =>
  Object.entries(stepTypeMap).map(([key, val]) => ({
    text: `${val.icon} ${val.name}`,
    id: key
  }))
)

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

const openStepTypePicker = (idx) => {
  currentStepIndex.value = idx
  showStepTypePicker.value = true
}

const onStepTypeConfirm = ({ selectedOptions }) => {
  if (currentStepIndex.value >= 0) {
    form.value.steps[currentStepIndex.value].stepType = selectedOptions[0].id
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

const onCategoryConfirm = ({ selectedOptions }) => {
  form.value.categoryId = selectedOptions[0].id
  selectedCategoryName.value = selectedOptions[0].text
  showCategoryPicker.value = false
}

const onCraftConfirm = ({ selectedOptions }) => {
  form.value.craftTypeId = selectedOptions[0].id
  selectedCraftName.value = selectedOptions[0].text
  showCraftPicker.value = false
}

const submit = async () => {
  if (!userStore.isLoggedIn()) {
    showToast('请先登录')
    router.push('/login')
    return
  }

  if (!form.value.title) {
    showToast('请输入标题')
    return
  }
  if (!form.value.categoryId) {
    showToast('请选择品类')
    return
  }
  if (!fileList.value.length) {
    showToast('请上传成品图片')
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

onMounted(async () => {
  const [cats, crafts] = await Promise.all([
    getCategories('category'),
    getCategories('craft')
  ])
  categories.value = cats.map(c => ({ text: c.name, id: c.id }))
  craftTypes.value = crafts.map(c => ({ text: c.name, id: c.id }))
})
</script>

<style scoped>
.publish-page {
  min-height: 100vh;
  padding-bottom: 20px;
  background: #f5f5f5;
}

.publish-content {
  padding-top: 46px;
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
  align-items: center;
  gap: 6px;
  margin-bottom: 8px;
  background: #faf7f3;
  border-radius: 6px;
  padding: 4px;
}

.mat-name-input {
  flex: 2;
}

.mat-spec-input {
  flex: 1.5;
}

.mat-qty-input {
  flex: 1;
}

.mat-delete {
  color: #ee0a24;
  font-size: 18px;
  padding: 8px;
  cursor: pointer;
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
</style>
