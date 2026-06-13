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

        <van-cell-group inset title="制作过程图">
          <van-uploader
            v-model="processFileList"
            multiple
            :max-count="20"
            :after-read="afterProcessRead"
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

        <van-cell-group inset title="详细信息">
          <van-field
            v-model="form.materials"
            label="用料"
            type="textarea"
            placeholder="使用了哪些皮料、五金、线等..."
            rows="2"
          />
          <van-field
            v-model="form.craftSteps"
            label="制作心得"
            type="textarea"
            placeholder="分享制作过程中的技巧和心得..."
            rows="4"
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

const form = ref({
  title: '',
  content: '',
  materials: '',
  craftSteps: '',
  categoryId: null,
  craftTypeId: null
})

const fileList = ref([])
const processFileList = ref([])
const submitting = ref(false)
const showCategoryPicker = ref(false)
const showCraftPicker = ref(false)
const categories = ref([])
const craftTypes = ref([])
const selectedCategoryName = ref('')
const selectedCraftName = ref('')

const afterRead = (file) => {
  // 模拟上传
  fileList.value = fileList.value.concat(file)
  form.value.cover = file[0]?.content || ''
}

const afterProcessRead = (file) => {
  processFileList.value = processFileList.value.concat(file)
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
    
    await publishWork({
      ...form.value,
      cover: images[0],
      images,
      processImages
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
</style>
