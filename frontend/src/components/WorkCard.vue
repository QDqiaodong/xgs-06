<template>
  <div class="work-card" :class="density" @click="$router.push(`/work/${work.id}`)">
    <template v-if="density === 'compact'">
      <div class="compact-image">
        <img :src="work.cover || (work.images && work.images[0])" :alt="work.title" loading="lazy" />
        <div class="offline-badge" v-if="work.status === 0">已下架</div>
      </div>
      <div class="compact-content">
        <h3 class="compact-title">{{ work.title }}</h3>
        <div class="compact-tags">
          <span
            v-if="work.workStatus && getWorkStatusInfo(work.workStatus)"
            class="work-status-badge-tag"
            :class="getWorkStatusClass(work.workStatus)"
          >
            {{ getWorkStatusInfo(work.workStatus).icon }} {{ getWorkStatusInfo(work.workStatus).name }}
          </span>
          <span
            v-if="completionInfo"
            class="completion-badge-tag"
            :class="getCompletionClass(work.completionLevel)"
          >
            {{ completionInfo.icon }} {{ completionInfo.name }}
          </span>
          <van-tag v-if="work.categoryName" type="primary" size="medium" plain>
            {{ work.categoryName }}
          </van-tag>
          <span v-if="work.craftTypeName" class="craft-badge-tag" :class="getCraftClass(work.craftTypeName)">
            {{ getCraftInfoByName(work.craftTypeName).icon }} {{ work.craftTypeName }}
          </span>
          <span
            v-if="work.difficulty && getDifficultyInfo(work.difficulty)"
            class="difficulty-badge-tag"
            :class="getDifficultyClass(work.difficulty)"
          >
            {{ getDifficultyInfo(work.difficulty).icon }} {{ getDifficultyInfo(work.difficulty).name }}
          </span>
        </div>
        <div class="compact-completion-bar" v-if="completionInfo">
          <div class="compact-bar-bg">
            <div class="compact-bar-fill" :style="{ width: getMiniCompletionFill(), background: completionInfo.color }"></div>
          </div>
          <span class="compact-bar-percent" :style="{ color: completionInfo.borderColor }">{{ completionInfo.percent }}%</span>
        </div>
        <div class="compact-stats">
          <van-icon name="eye-o" /> {{ work.viewCount || 0 }}
          <van-icon name="star-o" /> {{ work.favoriteCount || 0 }}
        </div>
      </div>
    </template>

    <template v-else>
      <div class="card-image">
        <img :src="work.cover || (work.images && work.images[0])" :alt="work.title" loading="lazy" />
        <div class="offline-badge" v-if="work.status === 0">已下架</div>
      </div>
      <div class="card-content">
        <h3 class="card-title">{{ work.title }}</h3>
        <div class="card-meta">
          <div class="card-tags">
            <span
              v-if="work.workStatus && getWorkStatusInfo(work.workStatus)"
              class="work-status-badge-tag"
              :class="getWorkStatusClass(work.workStatus)"
            >
              {{ getWorkStatusInfo(work.workStatus).icon }} {{ getWorkStatusInfo(work.workStatus).name }}
            </span>
            <span
              v-if="completionInfo"
              class="completion-badge-tag"
              :class="getCompletionClass(work.completionLevel)"
            >
              {{ completionInfo.icon }} {{ completionInfo.name }} {{ completionInfo.percent }}%
            </span>
            <span class="category">{{ work.categoryName }}</span>
            <span v-if="work.craftTypeName" class="craft-badge-tag" :class="getCraftClass(work.craftTypeName)">
              {{ getCraftInfoByName(work.craftTypeName).icon }} {{ work.craftTypeName }}
            </span>
            <span
              v-if="work.difficulty && getDifficultyInfo(work.difficulty)"
              class="difficulty-badge-tag"
              :class="getDifficultyClass(work.difficulty)"
            >
              {{ getDifficultyInfo(work.difficulty).icon }} {{ getDifficultyInfo(work.difficulty).name }}
            </span>
          </div>
          <div class="card-completion-scale" v-if="completionInfo">
            <div class="card-scale-bg">
              <div class="card-scale-fill" :style="{ width: getMiniCompletionFill() }"></div>
              <div
                v-for="tick in COMPLETION_ORDER"
                :key="tick"
                class="card-scale-dot"
                :class="{ active: tick <= work.completionLevel, current: tick === work.completionLevel }"
                :style="{
                  borderColor: tick <= work.completionLevel ? COMPLETION_LEVELS[tick].borderColor : '#e0e0e0',
                  background: tick <= work.completionLevel ? COMPLETION_LEVELS[tick].color : '#fff'
                }"
              ></div>
            </div>
          </div>
          <span class="stats">
            <van-icon name="eye-o" /> {{ work.viewCount || 0 }}
            <van-icon name="star-o" /> {{ work.favoriteCount || 0 }}
          </span>
        </div>
        <div class="card-materials" v-if="work.materialBrief">
          <span class="material-brief">{{ work.materialBrief }}</span>
        </div>
        <div class="card-materials" v-else-if="work.materialSummary || work.materials">
          <MaterialSummary :material-summary="work.materialSummary" :materials-text="work.materials" compact />
        </div>
        <div class="card-author">
          <van-image
            round
            size="24"
            :src="work.avatar || 'https://img.yzcdn.cn/vant/cat.jpeg'"
          />
          <span class="nickname">{{ work.nickname }}</span>
        </div>
      </div>
    </template>
  </div>
</template>

<script setup>
import MaterialSummary from './MaterialSummary.vue'
import {
  getCraftInfoByName,
  getCraftClass,
  getDifficultyInfo,
  getDifficultyClass,
  getWorkStatusInfo,
  getWorkStatusClass,
  getCompletionInfo,
  getCompletionClass,
  COMPLETION_ORDER,
  COMPLETION_LEVELS
} from '@/utils/craftConfig'
import { computed } from 'vue'

const props = defineProps({
  work: {
    type: Object,
    required: true
  },
  density: {
    type: String,
    default: 'standard'
  }
})

const completionInfo = computed(() => getCompletionInfo(props.work.completionLevel))

const getMiniCompletionFill = () => {
  return `${completionInfo.value?.percent || 0}%`
}
</script>

<style scoped>
.work-card {
  background: #fff;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.work-card.compact {
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.compact-image {
  width: 100%;
  aspect-ratio: 1/1;
  overflow: hidden;
  background: #f0f0f0;
  position: relative;
}

.compact-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.compact-content {
  padding: 8px 10px;
}

.compact-title {
  font-size: 13px;
  font-weight: 500;
  margin: 0 0 6px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  color: #333;
}

.compact-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 4px;
  margin-bottom: 6px;
}

.compact-stats {
  display: flex;
  gap: 8px;
  align-items: center;
  font-size: 11px;
  color: #999;
}

.card-image {
  width: 100%;
  aspect-ratio: 4/3;
  overflow: hidden;
  background: #f0f0f0;
  position: relative;
}

.offline-badge {
  position: absolute;
  top: 8px;
  left: 8px;
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.compact .offline-badge {
  font-size: 10px;
  padding: 1px 6px;
  top: 6px;
  left: 6px;
}

.card-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.card-content {
  padding: 12px;
}

.card-title {
  font-size: 15px;
  font-weight: 500;
  margin: 0 0 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.card-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #999;
}

.card-tags {
  display: flex;
  gap: 6px;
  align-items: center;
  flex-wrap: wrap;
}

.card-materials {
  margin-bottom: 10px;
}

.material-brief {
  font-size: 12px;
  color: #666;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  background: #f8f4ef;
  padding: 6px 8px;
  border-radius: 6px;
  border-left: 3px solid #c08457;
}

.category {
  background: #f5e6d3;
  color: #8b6914;
  padding: 2px 8px;
  border-radius: 4px;
}

.stats {
  display: flex;
  gap: 8px;
  align-items: center;
}

.card-author {
  display: flex;
  align-items: center;
  gap: 8px;
}

.nickname {
  font-size: 13px;
  color: #666;
}

.craft-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 6px;
  border-radius: 4px;
  font-size: 11px;
  font-weight: 500;
  border: 1px solid transparent;
}

.difficulty-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 7px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid transparent;
}

.difficulty-badge-tag.difficulty-badge--beginner {
  background: #f6ffed;
  color: #389e0d;
  border-color: #b7eb8f;
}

.difficulty-badge-tag.difficulty-badge--intermediate {
  background: #fff7e6;
  color: #d46b08;
  border-color: #ffd591;
}

.difficulty-badge-tag.difficulty-badge--advanced {
  background: #fff1f0;
  color: #cf1322;
  border-color: #ffa39e;
}

.work-status-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 8px;
  border-radius: 12px;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid transparent;
}

.work-status-badge-tag.work-status-badge--practice {
  background: #fafafa;
  color: #8c8c8c;
  border-color: #d9d9d9;
}

.work-status-badge-tag.work-status-badge--finished {
  background: #f6ffed;
  color: #389e0d;
  border-color: #b7eb8f;
}

.work-status-badge-tag.work-status-badge--repair {
  background: #fff7e6;
  color: #d46b08;
  border-color: #ffd591;
}

.work-status-badge-tag.work-status-badge--semi_finished {
  background: #e6f7ff;
  color: #096dd9;
  border-color: #91d5ff;
}

.completion-badge-tag {
  display: inline-flex;
  align-items: center;
  gap: 2px;
  padding: 1px 7px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: 600;
  border: 1px solid transparent;
}

.completion-badge-tag.completion-badge--L1 {
  background: #fafafa;
  color: #8c8c8c;
  border-color: #d9d9d9;
}

.completion-badge-tag.completion-badge--L2 {
  background: #e6f7ff;
  color: #096dd9;
  border-color: #91d5ff;
}

.completion-badge-tag.completion-badge--L3 {
  background: #fff7e6;
  color: #d46b08;
  border-color: #ffd591;
}

.completion-badge-tag.completion-badge--L4 {
  background: #f6ffed;
  color: #389e0d;
  border-color: #b7eb8f;
}

.completion-badge-tag.completion-badge--L5 {
  background: #d9f7be;
  color: #237804;
  border-color: #73d13d;
}

.compact-completion-bar {
  display: flex;
  align-items: center;
  gap: 6px;
  margin-bottom: 6px;
}

.compact-bar-bg {
  flex: 1;
  height: 4px;
  background: #f0f0f0;
  border-radius: 2px;
  overflow: hidden;
}

.compact-bar-fill {
  height: 100%;
  border-radius: 2px;
  transition: width 0.4s ease;
}

.compact-bar-percent {
  font-size: 10px;
  font-weight: 700;
  flex-shrink: 0;
  width: 28px;
  text-align: right;
}

.card-completion-scale {
  width: 100%;
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px dashed #f0e6d8;
}

.card-scale-bg {
  position: relative;
  height: 5px;
  background: #f5f5f5;
  border-radius: 3px;
  overflow: visible;
}

.card-scale-fill {
  position: absolute;
  top: 0;
  left: 0;
  height: 100%;
  border-radius: 3px;
  background: linear-gradient(90deg, #95de64, #237804);
  transition: width 0.4s ease;
}

.card-scale-dot {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  width: 11px;
  height: 11px;
  border-radius: 50%;
  border: 2px solid;
  transition: all 0.3s ease;
  z-index: 2;
}

.card-scale-dot:nth-child(2) { left: 2%; }
.card-scale-dot:nth-child(3) { left: 26%; }
.card-scale-dot:nth-child(4) { left: 50%; }
.card-scale-dot:nth-child(5) { left: 74%; }
.card-scale-dot:nth-child(6) { left: 96%; }

.card-scale-dot.current {
  transform: translateY(-50%) scale(1.35);
  box-shadow: 0 0 0 2px rgba(115, 209, 61, 0.25);
}

.card-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-start !important;
  justify-content: flex-start !important;
  gap: 8px;
  margin-bottom: 8px;
}

.card-meta > .card-tags {
  width: 100%;
}

.card-meta > .card-completion-scale {
  width: 100%;
}

.card-meta > .stats {
  align-self: flex-end;
  margin-top: -4px;
}
</style>
