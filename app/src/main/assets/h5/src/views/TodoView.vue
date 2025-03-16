<template>
  <el-row class="todo-container">
    <el-row class="todo-header">
      {{ currentYearMonth }}{{ currentDay }}日
    </el-row>
    <el-row class="todo-content">
      <el-scrollbar class="content" v-if="todos.length > 0">
        <row :class="todo.id===2?'finished item':'item'"  v-for="todo in todos" :key="todo.id">
          <div class="time">
            {{ todo.time }}
          </div>
          <div class="txt">
            <span :class="`tag leve${todo.level}`"></span>
            <div class="title ">
              {{ todo.title }}
            </div>
          </div>
          <div class="checkbox">
            <el-checkbox v-model="checked1" size="large" />
          </div>
        </row>
      </el-scrollbar>
      <el-empty v-else style="height: 100%;width: 100%;" description="没有任务" />
    </el-row>
    <!-- <div class="todo-content">
      <template v-if="todos.length > 0">
        <div v-for="todo in todos" :key="todo.id" class="todo-item">
          <el-checkbox v-model="todo.completed" class="todo-checkbox" />
          <div class="todo-info">
            <div class="todo-title" :class="{ completed: todo.completed }">
              {{ todo.title }}
            </div>
            <div class="todo-time" v-if="todo.datetime">
              {{ formatDateTime(todo.datetime) }}
            </div>
          </div>
          <div class="todo-actions">
            <el-button link type="danger" @click="deleteTodo(todo.id)">
              <el-icon><Delete /></el-icon>
            </el-button>
          </div>
        </div>
      </template>
      <el-empty v-else style="height: 100%;" description="没有任务" />
    </div> -->
    <div 
      class="add-button"
      @click="dialogVisible = true"
      @touchstart="handleTouchStart"
      @touchend="handleTouchEnd"
    >
      <el-icon class="add-icon"><Plus /></el-icon>
    </div>
    <el-drawer
      class="drawer"
      v-model="dialogVisible"
      :with-header="false"
      title="新增任务"
      direction="btt"
      size="120px"
      :show-close="false"
    >
      <template #default>
        <div class="drawer-content">
          <el-form :model="newTodo">
            <el-form-item>
              <el-input
                style="width: calc(100% - 35px);"
                v-model="newTodo.title"
                placeholder="请输入任务名称"
              />
              <el-button
                v-show="newTodo.title.length > 0"
                link
                @click="addTodo"
                class="drawer-button">
                <el-icon><Promotion /></el-icon>
              </el-button>
            </el-form-item>
            <el-form-item>
              <el-button link @click="addTodo" class="drawer-button">
                <el-icon><Clock /></el-icon>
              </el-button>
              <el-button link @click="addTodo" class="drawer-button" style="margin-left: 10px;">
                <el-icon><InfoFilled /></el-icon>
              </el-button>
            </el-form-item>
          </el-form>
        </div>
      </template>
    </el-drawer>
  </el-row>
</template>

<script setup>
import * as v from 'vue'
import WeekDisplay from '../components/WeekDisplay.vue'
import { Plus, Position, Promotion, Clock, InfoFilled, Delete } from '@element-plus/icons-vue'

const todos = v.ref([
  {
    id: 1,
    title: '重要且紧急',
    type: 'important-urgent',
    level: '1',
    time: '14:00',
  },
  {
    id: 2,
    title: '重要不紧急',
    type: 'important-not-urgent',
    level: '2',
    time: '14:00',
  },
  {
    id: 3,
    title: '紧急不重要',
    type: 'urgent-not-important',
    level: '3',
    time: '14:00',
  },
  {
    id: 4,
    title: '不重要不紧急',
    type: 'urgent-not-important',
    level: '4',
    time: '14:00',
  }
])
const checkedTodos = v.ref([])
const dialogVisible = v.ref(false)
const newTodo = v.ref({
  title: '',
  datetime: ''
})
const weekDisplayRef = v.ref(null)

const currentDate = v.ref(new Date())

const currentYearMonth = v.computed(() => {
  return `${currentDate.value.getFullYear()}年${currentDate.value.getMonth() + 1}月`
})

const currentDay = v.computed(() => {
  return currentDate.value.getDate()
})

// 监听选中状态变化
v.watch(checkedTodos, (newVal) => {
  todos.value = todos.value.map(todo => ({
    ...todo,
    completed: newVal.includes(todo.id)
  }))
})

// 添加待办事项
const addTodo = () => {
  if (newTodo.value.title.trim()) {
    todos.value.push({
      id: Date.now(),
      title: newTodo.value.title,
      datetime: newTodo.value.datetime,
      completed: false
    })
    newTodo.value.title = ''
    newTodo.value.datetime = ''
    dialogVisible.value = false
  }
}

// 格式化日期时间显示
const formatDateTime = (datetime) => {
  if (!datetime) return ''
  return datetime
}
// 删除待办事项
const deleteTodo = (id) => {
  todos.value = todos.value.filter(todo => todo.id !== id)
  checkedTodos.value = checkedTodos.value.filter(todoId => todoId !== id)
}

// 调用子组件的重置方法
const resetWeekDisplay = () => {
  weekDisplayRef.value?.resetToCurrentDay()
}

// 添加触摸效果处理
const handleTouchStart = (e) => {
  e.currentTarget.style.transform = 'scale(0.9)'
}

const handleTouchEnd = (e) => {
  e.currentTarget.style.transform = 'scale(1)'
}
</script>

<style scoped lang="scss">
:deep(.el-scrollbar__wrap.el-scrollbar__wrap--hidden-default) {
  width: 100%;
}
:deep(.el-scrollbar__wrap.el-scrollbar__wrap--hidden-default .el-scrollbar__view) {
  width: 100%;
  height: 100%;
}
</style>
<style scoped lang="scss">
.todo-container {
  height: 100%;
  width: 100%;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;

  .todo-header {
    padding: 10px;
    background-color: var(--bg-color);
    font-size: 17px;
    box-shadow: 0 0 12px rgba(0, 0, 0, 0.1);
    border-bottom: 1px solid #e4e7ed;
  }

  .todo-content {
    flex: 1;
    background-color: #f9fafb;
    z-index: 1;
    padding: 10px;
    
    .content {
      display: flex;
      height: 100%;
      width: 100%;
      
      .finished {
        opacity: 0.7;
        .txt {
          .title {
            text-decoration: line-through;
            color: #909399;
          }
        }
      }

      .item {
        display: flex;
        width: 100%;
        height: 70px;
        margin-bottom: 10px;
        background-color: #fff;
        box-shadow: 0 0px 7px rgba(0, 0, 0, 0.1);
        border-radius: 8px;
        border: 1px solid #e4e7ed;

        .time {
          letter-spacing: 1px;
          display: flex;
          align-items: center;
          justify-content: center;
          width: 70px;
          font-size: 14px;
          color: #a1a1a1;
       }

       .txt {
          display: flex;
          align-items: center;
          width: calc(100% - 140px);
          font-size: 16px;
          color: var(--text-color);
         .tag {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            margin-right: 4px;
         }
         .leve1 {
            background-color: #F56C6C;
         }

        .leve2 {
            background-color: #E6A23C;
        }

        .leve3 {
            background-color: #67C23A;
         }
         .leve4 {
            background-color: #909399;
         }
        .title {
            letter-spacing: 1px;
            margin-left: 1px;
            display: flex;
            font-size: 15px;
            color: var(--text-color);
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
         }
       }

      .checkbox {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 70px;
          font-size: 16px;
          color: var(--text-color);
       }
      }
    }
  }
}

.date-info {
  display: flex;
  align-items: center;
}

.current-date {
  font-size: 16px;
  color: var(--text-color);
}
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.todo-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}
.todo-item:last-child {
  border-bottom: none;
}
.completed {
  text-decoration: line-through;
  color: #999;
}
.add-button {
  position: fixed;
  right: 20px;
  bottom: 80px;
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background-color: var(--active-color);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  cursor: pointer;
  transition: all 0.3s ease;
  z-index: 10;
  -webkit-tap-highlight-color: transparent;
  user-select: none;
  touch-action: manipulation;
}
.add-button:active {
  transform: scale(0.9);
  background-color: var(--active-color);
}
.add-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  border-radius: 50%;
  pointer-events: none;
}
.add-icon {
  font-size: 24px;
  color: #fff;
}
/* 适配暗色模式 */
@media (prefers-color-scheme: dark) {
  .add-button {
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  }
}
:deep(.drawer .el-drawer__body){
  padding: 10px;
}
.drawer-content {
  padding-top: 10px;
}
.drawer-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 16px 20px;
  border-top: 1px solid var(--border-color);
}
.drawer-button{
  font-size: 24px;color: var(--active-color);
}
:deep(.el-input__wrapper) {
  box-shadow: none !important;
  padding: 0;
}
:deep(.el-input__inner) {
  border: none;
  padding: 0;
  font-size: 16px;
}
.todo-checkbox {
  margin-right: 12px;
}

.todo-info {
  flex: 1;
  margin-right: 16px;
}

.todo-actions {
  display: flex;
  align-items: center;
}
:deep(.el-checkbox__input.is-checked .el-checkbox__inner) {
  background-color: var(--active-color);
  border-color: var(--active-color);
}
</style>