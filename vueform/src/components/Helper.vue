<template>
  <div class="qa-assistant">
    <!-- 课程选择 -->
    <div class="course-select" v-if="courseList.length > 0">
      <el-select v-model="selectedCourseId" placeholder="请选择课程" @change="handleCourseChange">
        <el-option
            v-for="course in courseList"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
        />
      </el-select>
    </div>

    <!-- 无课程提示 -->
    <div v-else class="no-course-tip">
      <el-empty description="您还没有选择任何课程" />
    </div>

    <!-- 问答界面 -->
    <div v-if="selectedCourseId" class="qa-interface">
      <!-- 聊天记录区域 -->
      <div class="chat-history" ref="chatHistory">
        <div v-for="record in sortedQaRecords" :key="record.qaId" class="chat-message">
          <!-- 问题 -->
          <div class="question-box">
            <div class="avatar">
              <el-avatar :size="40" icon="el-icon-user"></el-avatar>
            </div>
            <div class="message-content">
              <div class="user-info">{{ userInfo?.name || '我' }}</div>
              <div class="message">{{ record.question }}</div>
              <div class="time">{{ formatTime(record.createdAt) }}</div>
            </div>
          </div>
          <!-- 回答 -->
          <div class="answer-box">
            <div class="avatar">
              <el-avatar :size="40">AI</el-avatar>
            </div>
            <div class="message-content">
              <div class="message" v-html="formatAnswer(record.answer)"></div>
              <div class="time">{{ formatTime(record.createdAt) }}</div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="input-area">
        <el-form :model="questionForm" ref="questionForm" :rules="rules">
          <el-form-item prop="content">
            <el-input
                type="textarea"
                v-model="questionForm.content"
                :rows="3"
                :placeholder="placeholder"
                @keyup.enter.native.ctrl="handleSend"
            />
          </el-form-item>
          <el-form-item>
            <div class="button-group">
              <el-button
                  type="primary"
                  :loading="sending"
                  @click="handleSend"
              >发送 (Ctrl + Enter)</el-button>
              <el-button @click="clearInput">清空</el-button>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script>
import dayjs from 'dayjs'

export default {
  name: 'QaAssistant',
  data() {
    return {
      courseList: [],
      selectedCourseId: null,
      qaRecords: [],
      questionForm: {
        content: ''
      },
      rules: {
        content: [
          { required: true, message: '请输入问题内容', trigger: 'blur' }
        ]
      },
      sending: false,
      userInfo: null,
      placeholder: '请输入您的问题，按 Ctrl + Enter 发送'
    }
  },

  computed: {
    // 对聊天记录按时间升序排序的计算属性
    sortedQaRecords() {
      return [...this.qaRecords].sort((a, b) =>
          new Date(a.createdAt) - new Date(b.createdAt)
      )
    }
  },

  created() {
    this.loadUserInfo()
  },

  methods: {
    // 获取当前用户信息
    getCurrentUser() {
      const userStr = sessionStorage.getItem("CurUser")
      if (userStr) {
        try {
          const userData = JSON.parse(userStr)
          return userData.data || userData
        } catch (error) {
          console.error('解析用户信息失败:', error)
          return null
        }
      }
      return null
    },

    // 加载用户信息
    async loadUserInfo() {
      try {
        const curUser = this.getCurrentUser()
        if (!curUser) {
          this.$message.error('未获取到用户信息，请重新登录')
          return
        }
        this.userInfo = curUser
        // 加载该用户的课程列表
        await this.loadCourseList()
      } catch (error) {
        console.error('获取用户信息失败：', error)
        this.$message.error('获取用户信息失败，请重新登录')
      }
    },

    // 加载课程列表
    async loadCourseList() {
      try {
        if (!this.userInfo?.id) return

        const res = await this.$axios.get(
            this.$httpUrl + '/course/student/courses/' + this.userInfo.id
        )

        if (res.data.code === 200) {
          this.courseList = res.data.data
          if (this.courseList.length > 0) {
            this.selectedCourseId = this.courseList[0].courseId
            this.loadHistory()
          }
        }
      } catch (error) {
        console.error('加载课程列表失败:', error)
        this.$message.error('加载课程列表失败')
      }
    },

    // 课程切换处理
    handleCourseChange(courseId) {
      this.selectedCourseId = courseId
      this.loadHistory()
    },

    // 加载历史记录
    async loadHistory() {
      try {
        if (!this.userInfo?.id || !this.selectedCourseId) return

        const res = await this.$axios.get(this.$httpUrl + '/api/course/qa/history', {
          params: {
            studentId: this.userInfo.id,
            courseId: this.selectedCourseId
          }
        })

        if (res.data.code === 200) {
          // 更新qaRecords数组
          this.qaRecords = res.data.data
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        }
      } catch (error) {
        console.error('加载历史记录失败:', error)
        this.$message.error('加载历史记录失败')
      }
    },

    // 发送问题
    async handleSend() {
      try {
        await this.$refs.questionForm.validate()

        if (!this.userInfo?.id || !this.selectedCourseId) {
          this.$message.error('用户信息获取失败')
          return
        }

        this.sending = true
        const res = await this.$axios.post(this.$httpUrl + '/api/course/qa/ask', null, {
          params: {
            courseId: this.selectedCourseId,
            studentId: this.userInfo.id,
            question: this.questionForm.content
          }
        })

        if (res.data.code === 200) {
          const newRecord = res.data.data
          this.qaRecords.push(newRecord)
          this.questionForm.content = ''
          this.$nextTick(() => {
            this.scrollToBottom()
          })
        } else {
          this.$message.error(res.data.msg || '发送失败')
        }
      } catch (error) {
        console.error('发送问题失败:', error)
        this.$message.error('发送失败，请重试')
      } finally {
        this.sending = false
      }
    },

    clearInput() {
      this.questionForm.content = ''
    },

    formatTime(time) {
      return dayjs(time).format('YYYY-MM-DD HH:mm:ss')
    },

    formatAnswer(answer) {
      if (!answer) return ''
      return answer.replace(/\n/g, '<br>')
    },

    scrollToBottom() {
      const container = this.$refs.chatHistory
      if (container) {
        container.scrollTop = container.scrollHeight
      }
    }
  }
}
</script>

<style scoped>
.qa-assistant {
  height: calc(100vh - 100px); /* 减去导航栏高度 */
  display: flex;
  flex-direction: column;
  background-color: #f5f7fa;
  padding: 20px;
  box-sizing: border-box;
  min-height: 600px; /* 设置最小高度 */
}

.course-select {
  margin-bottom: 20px;
  flex-shrink: 0;
}

.qa-interface {
  flex: 1;
  display: flex;
  flex-direction: column;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
  min-height: 0;
}

.chat-history {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
  background-color: #fff;
  min-height: 0;
  height: calc(100vh - 280px); /* 调整高度以适配其他元素 */
  margin-bottom: 10px;
}

.chat-history::-webkit-scrollbar {
  width: 6px;
}

.chat-history::-webkit-scrollbar-thumb {
  background-color: #dcdfe6;
  border-radius: 3px;
}

.chat-history::-webkit-scrollbar-track {
  background-color: #f5f7fa;
}

.input-area {
  flex-shrink: 0;
  background-color: #fff;
  padding: 20px;
  border-top: 1px solid #ebeef5;
}

.chat-message {
  margin-bottom: 24px;
}

.question-box,
.answer-box {
  display: flex;
  margin-bottom: 16px;
}

.avatar {
  margin-right: 12px;
  flex-shrink: 0;
}

.message-content {
  flex: 1;
  max-width: 80%;
}

.user-info {
  font-size: 14px;
  color: #666;
  margin-bottom: 4px;
}

.message {
  padding: 12px 16px;
  border-radius: 8px;
  word-break: break-word;
  line-height: 1.6;
}

.question-box .message {
  background-color: #ecf5ff;
}

.answer-box .message {
  background-color: #f4f4f5;
}

.time {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.button-group {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.no-course-tip {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

/* 适配移动设备 */
@media screen and (max-width: 768px) {
  .qa-assistant {
    padding: 10px;
  }

  .message-content {
    max-width: 90%;
  }

  .button-group {
    flex-direction: column;
    gap: 8px;
  }

  .chat-history {
    height: calc(100vh - 320px);
  }
}
</style>