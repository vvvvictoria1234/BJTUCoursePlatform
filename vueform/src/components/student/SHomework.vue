<template>
  <div class="homework-management">
    <!-- 搜索栏 -->
    <div class="search-bar">
      <el-select v-model="searchForm.courseId" placeholder="请选择课程" style="width: 200px">
        <el-option
            v-for="course in courseList"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
        />
      </el-select>
      <el-button type="primary" @click="loadHomeworkList" style="margin-left: 10px">查询</el-button>
    </div>

    <!-- 作业列表 -->
    <el-table :data="homeworkList" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="homeworkId" label="作业ID" width="100" />
      <el-table-column prop="title" label="作业标题" width="180" />
      <el-table-column prop="description" label="作业描述" show-overflow-tooltip />
      <el-table-column prop="deadline" label="截止日期" width="180" />
      <el-table-column prop="startTime" label="发布时间" width="180" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column label="状态" width="100">
        <template slot-scope="scope">
          <el-tag :type="getStatusType(scope.row)">{{ getStatusText(scope.row) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="280" fixed="right">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="submitHomework(scope.row)"
              :disabled="isSubmitDisabled(scope.row)"
          >提交作业</el-button>
          <el-button
              size="mini"
              type="info"
              @click="viewSubmission(scope.row)"
          >查看详情</el-button>
          <el-button
              v-if="scope.row.attachmentPath"
              size="mini"
              type="success"
              @click="downloadHomeworkFile(scope.row)"
          >下载附件</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 提交作业对话框 -->
    <el-dialog
        title="提交作业"
        :visible.sync="submitDialog.visible"
        width="50%"
    >
      <el-form
          ref="submitForm"
          :model="submitForm"
          :rules="submitRules"
          label-width="80px"
      >
        <el-form-item label="作业内容" prop="content">
          <el-input
              type="textarea"
              v-model="submitForm.content"
              :rows="6"
              placeholder="请输入作业内容"
          />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
              :action="$httpUrl + '/api/upload'"
              :on-success="handleUploadSuccess"
              :before-upload="beforeUpload"
              :limit="1"
          >
            <el-button size="small" type="primary">点击上传</el-button>
            <div slot="tip" class="el-upload__tip">支持PDF、Word、PPT等格式，大小不超过100MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="submitDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveSubmission">确定</el-button>
      </div>
    </el-dialog>

    <!-- 作业详情对话框 -->
    <el-dialog
        title="作业详情"
        :visible.sync="detailDialog.visible"
        width="50%"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="作业标题">{{ detailForm.title }}</el-descriptions-item>
        <el-descriptions-item label="截止日期">{{ detailForm.deadline }}</el-descriptions-item>
        <el-descriptions-item label="得分" :contentStyle="{color: detailForm.score ? '#67C23A' : '#909399'}">
          {{ detailForm.score || '未批改' }}
        </el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ detailForm.submitTime }}</el-descriptions-item>
      </el-descriptions>

      <!-- 作业要求和教师上传的附件信息 -->
      <div class="detail-content">
        <h4>作业要求：</h4>
        <p>{{ detailForm.description }}</p>
      </div>
      <div v-if="detailForm.homeworkAttachmentPath" class="detail-attachment">
        <h4>作业附件：</h4>
        <el-button type="text" @click="downloadAttachment(detailForm.homeworkAttachmentPath)">
          下载作业附件
        </el-button>
      </div>

      <!-- 学生提交的内容和附件 -->
      <div class="detail-content" v-if="detailForm.content">
        <h4>我的提交：</h4>
        <p>{{ detailForm.content }}</p>
      </div>
      <div v-if="detailForm.attachmentPath" class="detail-attachment">
        <h4>我的附件：</h4>
        <el-button type="text" @click="downloadAttachment(detailForm.attachmentPath)">
          下载我的附件
        </el-button>
      </div>

      <!-- 教师评语 -->
      <div class="detail-feedback" v-if="detailForm.feedback">
        <h4>教师评语：</h4>
        <p>{{ detailForm.feedback }}</p>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'StudentHomeworkManagement',
  data() {
    return {
      // 学生信息
      studentInfo: {
        id: '',
        name: '',
        phone: ''
      },
      // 搜索表单
      searchForm: {
        courseId: ''
      },
      // 课程列表
      courseList: [],
      // 作业列表
      homeworkList: [],
      // 提交对话框
      submitDialog: {
        visible: false
      },
      // 提交表单
      submitForm: {
        homeworkId: '',
        content: '',
        attachmentPath: '',
        studentId: ''
      },
      // 提交表单验证规则
      submitRules: {
        content: [{ required: true, message: '请输入作业内容', trigger: 'blur' }]
      },
      // 详情对话框
      detailDialog: {
        visible: false
      },
      // 详情表单
      detailForm: {
        title: '',
        deadline: '',
        score: null,
        submitTime: '',
        content: '',
        feedback: '',
        attachmentPath: '',
        description: '',
        homeworkAttachmentPath: ''
      }
    }
  },

  created() {
    const curUser = this.getCurrentUser()
    if (curUser) {
      this.studentInfo = curUser
    }
    this.loadUserInfo()
    this.loadCourseList()
    this.loadHomeworkList()
  },

  methods: {
    // 获取当前用户信息
    getCurrentUser() {
      const userStr = sessionStorage.getItem("CurUser")
      if (userStr) {
        const userData = JSON.parse(userStr)
        return userData.data || userData
      }
      return null
    },

    async loadUserInfo() {
      try {
        const curUser = this.getCurrentUser()
        if (!curUser || !curUser.id) {
          this.$message.error('未获取到用户信息，请重新登录')
          return
        }

        const res = await this.$axios.post(this.$httpUrl + '/user/listPageC1', {
          pageSize: 10,
          pageNum: 1,
          param: {
            id: curUser.id
          }
        })

        if (res.data.code === 200 && res.data.data.length > 0) {
          this.studentInfo = res.data.data[0]
          curUser.name = this.studentInfo.name
          curUser.phone = this.studentInfo.phone
          sessionStorage.setItem("CurUser", JSON.stringify(curUser))
        } else {
          this.studentInfo = curUser
        }
      } catch (error) {
        console.error('获取用户信息失败：', error)
        const curUser = this.getCurrentUser()
        if (curUser) {
          this.studentInfo = curUser
        } else {
          this.$message.error('获取用户信息失败，请重新登录')
        }
      }
    },

    // 加载课程列表
    async loadCourseList() {
      try {
        const res = await this.$axios.get(this.$httpUrl + '/course/student/courses/' + this.studentInfo.id)
        if (res.data.code === 200) {
          this.courseList = res.data.data
        }
      } catch (error) {
        console.error('加载课程列表失败:', error)
        this.$message.error('获取课程列表失败')
      }
    },

    // 加载作业列表
    async loadHomeworkList() {
      try {
        const params = {
          pageNum: 1,
          pageSize: 100,
          param: {
            courseId: this.searchForm.courseId ? parseInt(this.searchForm.courseId) : null
          }
        }
        const res = await this.$axios.post(this.$httpUrl + '/homework/list', params)
        if (res.data.code === 200) {
          this.homeworkList = res.data.data
        }
      } catch (error) {
        console.error('加载作业列表失败:', error)
        this.$message.error('获取作业列表失败')
      }
    },

    // 提交作业
    submitHomework(homework) {
      this.submitForm = {
        homeworkId: homework.homeworkId,
        content: '',
        attachmentPath: '',
        studentId: this.studentInfo.id
      }
      this.submitDialog.visible = true
    },

    // 保存提交
    async saveSubmission() {
      try {
        const valid = await this.$refs.submitForm.validate()
        if (!valid) return

        const res = await this.$axios.post(this.$httpUrl + '/homework/submit', this.submitForm)
        if (res.data.code === 200) {
          this.$message.success('作业提交成功')
          this.submitDialog.visible = false
          this.loadHomeworkList()
        }
      } catch (error) {
        console.error('提交作业失败:', error)
        this.$message.error('作业提交失败')
      }
    },

    // 查看提交详情
    async viewSubmission(homework) {
      try {
        // 获取作业提交详情
        const submissionRes = await this.$axios.get(
            this.$httpUrl + `/homework/submission/detail/${homework.homeworkId}`, {
              params: {
                studentId: this.studentInfo.id
              }
            }
        );

        // 获取作业详情
        const homeworkRes = await this.$axios.get(
            this.$httpUrl + `/homework/detail/${homework.homeworkId}`
        );

        // 合并数据
        if (homeworkRes.data.code === 200) {
          const homeworkData = homeworkRes.data.data;
          this.detailForm = {
            title: homework.title,
            deadline: homework.deadline,
            description: homeworkData.description,
            homeworkAttachmentPath: homeworkData.attachmentPath
          };

          // 如果有提交记录，添加提交信息
          if (submissionRes.data.code === 200 && submissionRes.data.data) {
            const submissionData = submissionRes.data.data;
            this.detailForm = {
              ...this.detailForm,
              content: submissionData.content,
              score: submissionData.score,
              submitTime: submissionData.submitTime,
              feedback: submissionData.feedback,
              attachmentPath: submissionData.attachmentPath
            };
          }

          this.detailDialog.visible = true;
        }
      } catch (error) {
        console.error('获取详情失败:', error);
        this.$message.error('获取详情失败');
      }
    },

    // 下载作业附件
    downloadHomeworkFile(homework) {
      if (homework.attachmentPath) {
        this.downloadAttachment(homework.attachmentPath)
      } else {
        this.$message.warning('该作业没有附件')
      }
    },

    // 处理文件上传成功
    handleUploadSuccess(response) {
      if (response.code === 200) {
        this.submitForm.attachmentPath = response.data
        this.$message.success('文件上传成功')
      }
    },

    // 上传前校验
    beforeUpload(file) {
      const isLt100M = file.size / 1024 / 1024 < 100
      if (!isLt100M) {
        this.$message.error('文件大小不能超过 100MB!')
      }
      return isLt100M
    },

    // 下载文件
    async downloadAttachment(path) {
      try {
        const response = await this.$axios.get(path, {
          responseType: 'blob'
        })
        const fileName = path.substring(path.lastIndexOf('/') + 1)
        const blob = new Blob([response.data])
        const downloadLink = document.createElement('a')
        downloadLink.href = URL.createObjectURL(blob)
        downloadLink.download = fileName
        document.body.appendChild(downloadLink)
        downloadLink.click()
        document.body.removeChild(downloadLink)
        URL.revokeObjectURL(downloadLink.href)
        this.$message.success('下载成功')
      } catch (error) {
        console.error('下载失败:', error)
        this.$message.error('下载失败')
      }
    },

    // 获取状态样式
    getStatusType(homework) {
      if (homework.status === 1) return 'success'
      if (new Date(homework.deadline) < new Date()) return 'danger'
      return 'warning'
    },

    // 获取状态文本
    getStatusText(homework) {
      if (homework.status === 1) return '已提交'
      if (new Date(homework.deadline) < new Date()) return '已截止'
      return '未提交'
    },

    // 判断是否禁用提交按钮
    isSubmitDisabled(homework) {
      return homework.status === 1 || new Date(homework.deadline) < new Date()
    }
  }
}
</script>

<style scoped>
.homework-management {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.detail-content,
.detail-feedback {
  margin-top: 20px;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
}

.detail-content h4,
.detail-feedback h4,
.detail-attachment h4 {
  margin: 10px 0;
  color: #303133;
  font-size: 14px;
}

.detail-content p,
.detail-feedback p {
  margin: 0;
  color: #606266;
  line-height: 1.6;
}

.detail-attachment {
  margin: 10px 0;
  padding: 10px;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.el-button[type="text"] {
  font-size: 13px;
  padding: 0;
}

.statistics-item {
  margin: 10px 0;
  display: flex;
  justify-content: space-between;
}

.statistics-label {
  font-weight: bold;
  color: #606266;
}

.statistics-value {
  color: #409EFF;
}

.el-descriptions {
  margin: 20px 0;
}

.el-descriptions-item {
  padding: 10px 20px;
}

.el-upload__tip {
  line-height: 1.2;
  margin-top: 5px;
  color: #909399;
}
</style>