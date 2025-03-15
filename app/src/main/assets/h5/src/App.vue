<template>
  <div class="app-container">
    <!-- 内容区域 -->
    <div class="content" @touchmove.prevent>
      <TodoView v-if="activeIndex === 'todo'" />
    </div>

    <!-- 底部导航栏 -->
    <div class="bottom-nav">
      <div 
        v-for="item in navItems" 
        :key="item.index"
        :class="['nav-item', { active: activeIndex === item.index }]"
        @click="handleSelect(item.index)"
      >
        <el-icon class="nav-icon">
          <component :is="item.icon" />
        </el-icon>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import TodoView from './views/ScheduleView.vue'
import { Checked, Grid, Calendar, Timer } from '@element-plus/icons-vue'

const activeIndex = ref('todo')
const navItems = [
  { index: 'todo', icon: Checked },
  { index: 'quadrant', icon: Grid },
  { index: 'habits', icon: Calendar },
  { index: 'pomodoro', icon: Timer }
]

const handleSelect = (key) => {
  activeIndex.value = key
}
</script>

<style>
:root {
  --bg-color: #ffffff;
  --nav-bg-color: #ffffff;
  --border-color: #eee;
  --text-color: #303133;
  --icon-color: #909399;
  --active-color: #409EFF;
}

/* @media (prefers-color-scheme: dark) {
  :root {
    --bg-color: #1a1a1a;
    --nav-bg-color: #1a1a1a;
    --border-color: #2c2c2c;
    --text-color: #e5eaf3;
    --icon-color: #909399;
    --active-color: #409EFF;
  }
} */

.app-container {
  display: flex;
  flex-direction: column;
  height: 100vh;
  width: 100vw;
  background-color: var(--bg-color);
  color: var(--text-color);
  overflow: hidden;
  position: fixed;
  top: 0;
  left: 0;
}

.content {
  height: calc(100vh - 60px);
  overflow: hidden;
  touch-action: none;
}

.bottom-nav {
  position: fixed;
  bottom: 0;
  width: 100%;
  height: 60px;
  display: flex;
  justify-content: space-around;
  align-items: center;
  background-color: var(--nav-bg-color);
  border-top: 1px solid var(--border-color);
}

.nav-item {
  width: 25%;
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.nav-icon {
  font-size: 24px;
  margin-bottom: 4px;
  color: var(--icon-color);
}

.nav-item.active .nav-icon {
  color: var(--active-color);
}

/* 点击效果 */
.nav-item:active {
  opacity: 0.7;
}
</style>
