<template>
  <div 
    class="calendar-container"
    @touchstart="handleTouchStart"
    @touchmove="handleTouchMove"
    @touchend="handleTouchEnd"
  >
    <!-- 日历头部 -->
    <div class="calendar-header">
      <div class="week-names">
        <div 
          v-for="day in weekDays" 
          :key="day.short" 
          class="week-name"
        >
          {{ day.short }}
        </div>
      </div>
    </div>

    <!-- 日历主体 -->
    <div 
      class="calendar-body"
      :class="{ 'week-view': isWeekView }"
    >
      <div 
        v-for="(week, weekIndex) in calendarWeeks" 
        :key="weekIndex"
        class="calendar-week"
        :class="{ 'hidden': isWeekView && weekIndex !== currentWeekIndex }"
      >
        <div 
          v-for="(date, dayIndex) in week" 
          :key="dayIndex"
          :class="[
            'calendar-day',
            {
              'other-month': !date.currentMonth,
              'current-day': isCurrentDay(date),
              'selected-day': isSelectedDay(date)
            }
          ]"
          @click="selectDate(date)"
        >
          <span class="day-number">{{ date.day }}</span>
          <div 
            class="day-indicator"
            :class="{ 'show': shouldShowIndicator(date.day) }"
          ></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'

const props = defineProps({
  indicatorDates: {
    type: Array,
    default: () => []
  }
})

const weekDays = [
  { short: '一', full: '星期一' },
  { short: '二', full: '星期二' },
  { short: '三', full: '星期三' },
  { short: '四', full: '星期四' },
  { short: '五', full: '星期五' },
  { short: '六', full: '星期六' },
  { short: '日', full: '星期日' }
]

const currentDate = ref(new Date())
const selectedDate = ref(new Date())

// 计算日历显示的所有日期，按周分组
const calendarWeeks = computed(() => {
  const year = currentDate.value.getFullYear()
  const month = currentDate.value.getMonth()
  
  // 获取当月第一天和最后一天
  const firstDay = new Date(year, month, 1)
  const lastDay = new Date(year, month + 1, 0)
  
  const days = []
  
  // 添加上月末尾的日期
  const firstDayWeekday = firstDay.getDay() || 7
  const prevMonthLastDay = new Date(year, month, 0).getDate()
  for (let i = firstDayWeekday - 1; i > 0; i--) {
    days.push({
      day: prevMonthLastDay - i + 1,
      date: new Date(year, month - 1, prevMonthLastDay - i + 1),
      currentMonth: false
    })
  }
  
  // 添加当月的日期
  for (let i = 1; i <= lastDay.getDate(); i++) {
    days.push({
      day: i,
      date: new Date(year, month, i),
      currentMonth: true
    })
  }
  
  // 添加下月开头的日期
  const remainingDays = 42 - days.length // 保持6行
  for (let i = 1; i <= remainingDays; i++) {
    days.push({
      day: i,
      date: new Date(year, month + 1, i),
      currentMonth: false
    })
  }
  
  // 将日期按周分组
  const weeks = []
  for (let i = 0; i < days.length; i += 7) {
    weeks.push(days.slice(i, i + 7))
  }
  
  return weeks
})

// 判断是否是当前日期
const isCurrentDay = (date) => {
  const today = new Date()
  return date.currentMonth && 
         date.date.getDate() === today.getDate() &&
         date.date.getMonth() === today.getMonth() &&
         date.date.getFullYear() === today.getFullYear()
}

// 判断是否是选中日期
const isSelectedDay = (date) => {
  return date.date.getDate() === selectedDate.value.getDate() &&
         date.date.getMonth() === selectedDate.value.getMonth() &&
         date.date.getFullYear() === selectedDate.value.getFullYear()
}

// 选择日期
const selectDate = (date) => {
  selectedDate.value = date.date
}

// 判断是否显示指示器
const shouldShowIndicator = (day) => {
  return props.indicatorDates.includes(day)
}

const isWeekView = ref(true)

// 计算当前选中日期所在的周索引
const currentWeekIndex = computed(() => {
  const selectedWeek = calendarWeeks.value.findIndex(week => 
    week.some(date => isSelectedDay(date))
  )
  return selectedWeek !== -1 ? selectedWeek : 0
})

// 切换周/月视图
const toggleView = () => {
  isWeekView.value = !isWeekView.value
}

const touchStartY = ref(0)
const touchStartX = ref(0)
const isTransitioning = ref(false)

// 处理触摸事件
const handleTouchStart = (e) => {
  touchStartY.value = e.touches[0].clientY
  touchStartX.value = e.touches[0].clientX
}

const handleTouchMove = (e) => {
  if (isTransitioning.value) return

  const touchY = e.touches[0].clientY
  const touchX = e.touches[0].clientX
  const diffY = touchStartY.value - touchY
  const diffX = Math.abs(touchStartX.value - touchX)

  // 如果水平移动大于垂直移动，不处理
  if (diffX > Math.abs(diffY)) return

  // 防止页面滚动
  e.preventDefault()

  if (diffY > 50 && !isWeekView.value) {
    // 向上滑动且当前是月视图，收起为周视图
    isTransitioning.value = true
    isWeekView.value = true
    setTimeout(() => {
      isTransitioning.value = false
    }, 300)
  } else if (diffY < -50 && isWeekView.value) {
    // 向下滑动且当前是周视图，展开为月视图
    isTransitioning.value = true
    isWeekView.value = false
    setTimeout(() => {
      isTransitioning.value = false
    }, 300)
  }
}

const handleTouchEnd = () => {
  touchStartY.value = 0
  touchStartX.value = 0
}

// 暴露方法给父组件
defineExpose({
  toggleView
})
</script>

<style scoped>
.calendar-container {
  background-color: var(--bg-color);
  border-radius: 8px;
  overflow: hidden;
}

.calendar-header {
  padding: 16px 16px 8px;
}

.week-names {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin-bottom: 8px;
}

.week-name {
  text-align: center;
  font-size: 12px;
  color: var(--text-color);
  opacity: 0.7;
}

.calendar-body {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 0 16px 16px;
  transition: all 0.3s ease;
  overflow: hidden;
  height: auto;
}

.calendar-week {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 4px;
  transition: all 0.3s ease;
  opacity: 1;
  transform: translateY(0);
  height: 48px;
}

.calendar-week.hidden {
  opacity: 0;
  transform: translateY(-20px);
  height: 0;
  pointer-events: none;
  margin: 0;
  padding: 0;
}

.week-view {
  max-height: 55px;
  overflow: hidden;
}

.calendar-day {
  aspect-ratio: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 2px;
  cursor: pointer;
  position: relative;
}

.day-number {
  font-size: 14px;
  color: var(--text-color);
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.calendar-day:hover .day-number {
  background-color: var(--border-color);
}

.calendar-day.other-month {
  opacity: 0.3;
}

.calendar-day.current-day .day-number {
  border: 2px solid var(--active-color);
  color: var(--active-color);
}

.calendar-day.selected-day .day-number {
  background-color: var(--active-color);
  color: #fff;
}

.day-indicator {
  width: 4px;
  height: 4px;
  border-radius: 50%;
  background-color: var(--active-color);
  opacity: 0;
  transition: opacity 0.3s ease;
  position: absolute;
  bottom: 1px;
}

.day-indicator.show {
  opacity: 1;
}
</style> 