<template>
  <el-row class="todo-container">
    <el-row class="todo-header">
      <span>
        {{ currentYearMonth }}{{ currentDay }}日
      </span>
      <el-icon class="heard-icon"><Sunny /></el-icon>
    </el-row>
    <el-row class="todo-content">
      <el-scrollbar class="content" v-if="todos.length > 0">
        <row
          :class="todo.id===2?'finished item':'item'"
          v-for="todo in todos"
          :key="todo.id"
          @touchstart="handleItemTouchStart($event, todo)"
          @touchend="handleItemTouchEnd"
          >
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
    <div 
      class="add-button"
      @click="dialogVisible = true"
      @touchstart="handleTouchStart"
      @touchend="handleTouchEnd"
    >
      <el-icon class="add-icon"><Plus /></el-icon>
    </div>
    
    <!-- size="155" -->
    <el-drawer
      class="drawer"
      v-model="dialogVisible"
      :with-header="false"
      title="新增任务"
      direction="btt"
      size="100"
      :show-close="false"
    >
      <template #default>
        <div class="drawer-content">
          <el-form :model="newTodo">
            <el-form-item>
              <el-input
                class="title-input"
                v-model="newTodo.title"
                placeholder="请输入任务名称"
              />
              <!-- 
                v-show="newTodo.title.length > 0" -->
              <el-button
                link
                @click="addTodo"
                class="drawer-button">
                <el-icon><Promotion /></el-icon>
              </el-button>
            </el-form-item>
            <el-form-item style="border-bottom: 1px solid #e4e7ed;padding: 5px;">
              <el-button link @click="addTodo" class="param-btu">
                <el-icon><Clock /></el-icon>
              </el-button>
              <el-button link @click="addTodo" class="param-btu">
                <el-icon><WarnTriangleFilled /></el-icon>
              </el-button>
            </el-form-item>
            <!-- <el-form-item>
              <div class="date-dom">
                <span>是否选取结束日期</span>
                <el-date-picker
                  type="date"
                  placeholder="最后期限"
                  size="default"
                />
              </div>
              <el-switch v-model="value" />
            </el-form-item> -->
            <!-- <el-form-item>
              <el-radio-group v-model="radio" style="padding-left: 10px;">
                <el-radio :value="3"><el-icon class="type-icon type-icon-1"><WarnTriangleFilled /></el-icon></el-radio>
                <el-radio :value="6"><el-icon class="type-icon type-icon-2"><WarningFilled /></el-icon></el-radio>
                <el-radio :value="9"><el-icon class="type-icon type-icon-3"><InfoFilled /></el-icon></el-radio>
                <el-radio :value="9"><el-icon class="type-icon type-icon-4"><Checked /></el-icon></el-radio>
              </el-radio-group>
            </el-form-item> -->
          </el-form>
        </div>
      </template>
    </el-drawer>

    <!-- <el-dialog
    v-model="dialogVisible"
    title="Tips"
    width="500"
    :before-close="handleClose"
    >
      <span>This is a message</span>
      <template #footer>
        <div class="dialog-footer">
          <el-button @click="dialogVisible = false">Cancel</el-button>
          <el-button type="primary" @click="dialogVisible = false">
            Confirm
          </el-button>
        </div>
      </template>
    </el-dialog> -->
  </el-row>
</template>

<script setup>
import * as v from 'vue'

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

const actionDialogVisible = v.ref(false)
const longPressTimer = v.ref(null)

const handleItemTouchStart = (e, todo) => {
  longPressTimer.value = setTimeout(() => {
    actionDialogVisible.value = true
    alert('长按了')
  }, 500) // 500ms 长按时间
}
const handleItemTouchEnd = () => {
  if (longPressTimer.value) {
    clearTimeout(longPressTimer.value)
  }
}
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
:deep(.el-drawer__body) {
  padding: 0;
  height: 100%;
  overflow: hidden;
}
:deep(.el-input__wrapper) {
  box-shadow: none !important;
  background: none;
}
:deep(.el-radio) {
  margin-right: 50px;
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
    display: flex;
    justify-content: space-between;
    align-items: center;

   .heard-icon {
      font-size: 24px;
    }
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

    &:active {
      transform: scale(0.9);
      background-color: var(--active-color);
    }
    &:before {
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
  }
}
.drawer {
  .drawer-content {
    .el-form-item {
      margin-bottom: 10px;
      .title-input {
        width: calc(100% - 65px);
        margin: 10px;
        font-size: 16px;
      }
      .drawer-button {
        font-size: 30px;
        color: #409EFF;
      }
      .param-btu {
        font-size: 20px;
        margin-left: 10px;
      }
      .date-dom{
        width: calc(100% - 50px);

        span{
          letter-spacing: 1.5px;
          margin-left: 10px;
          font-size: 15px;
        }
      }
      .type-icon {
        font-size: 20px;
        margin-top: 10px;
      }
      .type-icon-1 {
        color: #F56C6C;
      }
      .type-icon-2 {
        color: #E6A23C;
      }
      .type-icon-3 {
        color: #67C23A;
      }
     .type-icon-4 {
        color: #909399;
      }

    }
  }
}
</style>