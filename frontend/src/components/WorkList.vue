<template>
  <div class="work-list">
    <van-pull-refresh v-model="refreshing" @refresh="onRefresh">
    <van-list
      v-model:loading="loading"
      :finished="finished"
      finished-text="没有更多了"
      @load="onLoad"
    >
      <div class="work-grid" :class="density">
        <van-swipe-cell v-if="type === 'my'" v-for="work in list" :key="work.id">
          <WorkCard :work="work" :density="density" />
          <template #right>
            <van-button square type="success" class="swipe-btn" text="上架" @click="handleOnline(work)" v-if="work.status === 0" />
            <van-button square type="warning" class="swipe-btn" text="下架" @click="handleOffline(work)" v-else />
            <van-button square type="danger" class="swipe-btn" text="删除" @click="handleDelete(work)" />
          </template>
        </van-swipe-cell>
        <WorkCard v-if="type !== 'my'" v-for="work in list" :key="work.id" :work="work" :density="density" />
      </div>
    </van-list>
    </van-pull-refresh>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import { getWorkPage, offlineWork, onlineWork, deleteWork } from '@/api'
import WorkCard from './WorkCard.vue'
import { useUserStore } from '@/store/user'
import { showToast, showConfirmDialog } from 'vant'

const props = defineProps({
  categoryId: {
    type: [Number, null],
    default: null
  },
  craftTypeId: {
    type: [Number, null],
    default: null
  },
  userId: {
    type: [Number, null],
    default: null
  },
  type: {
    type: String,
    default: 'all'
  },
  density: {
    type: String,
    default: 'standard'
  }
})

const userStore = useUserStore()
const list = ref([])
const loading = ref(false)
const finished = ref(false)
const refreshing = ref(false)
const page = ref(1)
const pageSize = 10

const fetchList = async (isRefresh = false) => {
  if (loading.value) {
    return
  }

  loading.value = true

  try {
    let data
    const currentPage = page.value

    if (props.type === 'my') {
      const { getMyWorks } = await import('@/api')
      data = await getMyWorks({
        page: currentPage,
        size: pageSize
      })
    } else if (props.type === 'favorite') {
      const { getFavoriteWorks } = await import('@/api')
      data = await getFavoriteWorks({
        page: currentPage,
        size: pageSize
      })
    } else {
      data = await getWorkPage({
        page: currentPage,
        size: pageSize,
        categoryId: props.categoryId,
        craftTypeId: props.craftTypeId
      })
    }

    const records = data?.records || []

    if (isRefresh) {
      list.value = records
    } else {
      list.value = [...list.value, ...records]
    }

    if (records.length < pageSize || currentPage >= data.pages || list.value.length >= data.total) {
      finished.value = true
    } else {
      page.value = currentPage + 1
    }
  } catch (e) {
    finished.value = true
  } finally {
    loading.value = false
    refreshing.value = false
  }
}

const handleOffline = async (work) => {
  try {
    await showConfirmDialog({ title: '确认下架', message: '下架后作品将仅自己可见，确认下架吗？' })
    await offlineWork(work.id)
    showToast('已下架')
    const target = list.value.find(w => w.id === work.id)
    if (target) target.status = 0
  } catch {}
}

const handleOnline = async (work) => {
  try {
    await showConfirmDialog({ title: '确认上架', message: '上架后作品将公开可见，确认上架吗？' })
    await onlineWork(work.id)
    showToast('已上架')
    const target = list.value.find(w => w.id === work.id)
    if (target) target.status = 1
  } catch {}
}

const handleDelete = async (work) => {
  try {
    await showConfirmDialog({ title: '确认删除', message: '删除后不可恢复，确认删除吗？' })
    await deleteWork(work.id)
    showToast('已删除')
    list.value = list.value.filter(w => w.id !== work.id)
  } catch {}
}

const onLoad = () => {
  fetchList()
}

const onRefresh = () => {
  page.value = 1
  finished.value = false
  fetchList(true)
}

watch(
  () => [props.categoryId, props.craftTypeId, props.userId, props.type, props.density],
  () => {
    list.value = []
    page.value = 1
    finished.value = false
    fetchList(true)
  },
  { immediate: true }
)
</script>

<style scoped>
.work-list {
  padding: 12px;
}

.work-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.work-grid.compact {
  grid-template-columns: repeat(3, 1fr);
  gap: 8px;
}

.swipe-btn {
  height: 100%;
}
</style>
