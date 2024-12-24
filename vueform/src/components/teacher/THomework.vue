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
      <el-button type="primary" @click="openHomeworkDialog()" style="margin-left: 10px">布置作业</el-button>
    </div>

    <!-- 作业列表 -->
    <el-table :data="homeworkList" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="homeworkId" label="作业ID" width="100" />
      <el-table-column prop="title" label="作业标题" width="180" />
      <el-table-column prop="description" label="作业描述" show-overflow-tooltip />
      <el-table-column prop="deadline" label="截止日期" width="180" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column label="操作" width="380" fixed="right">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="viewSubmissions(scope.row.homeworkId)"
          >查看提交</el-button>
          <el-button
              size="mini"
              type="success"
              @click="viewStats(scope.row.homeworkId)"
          >成绩统计</el-button>

          <el-button
              v-if="scope.row.attachmentPath"
              size="mini"
              type="info"
              @click="downloadAttachment(scope.row.attachmentPath)"
          >下载附件</el-button>

        </template>
      </el-table-column>
    </el-table>

    <!-- 布置作业对话框 -->
    <el-dialog
        :title="homeworkDialog.title"
        :visible.sync="homeworkDialog.visible"
        width="50%"
    >
      <el-form
          ref="homeworkForm"
          :model="homeworkForm"
          :rules="homeworkRules"
          label-width="100px"
      >
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="homeworkForm.courseId" placeholder="请选择课程">
            <el-option
                v-for="course in courseList"
                :key="course.courseId"
                :label="course.courseName"
                :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="作业标题" prop="title">
          <el-input v-model="homeworkForm.title" />
        </el-form-item>
        <el-form-item label="作业描述" prop="description">
          <el-input
              type="textarea"
              v-model="homeworkForm.description"
              :rows="4"
          />
        </el-form-item>
        <el-form-item label="截止日期" prop="deadline">
          <el-date-picker
              v-model="homeworkForm.deadline"
              type="datetime"
              placeholder="选择截止日期"
              value-format="yyyy-MM-dd HH:mm:ss"
              :picker-options="pickerOptions"
          />
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="homeworkForm.totalScore" :min="0" :max="100" />
        </el-form-item>
        <el-upload
            ref="upload"
            :action="$httpUrl + '/homework/upload'"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :limit="1"
            name="file"
        >
          <el-button size="small" type="primary">点击上传</el-button>
          <div slot="tip" class="el-upload__tip">支持PDF、Word、PPT等格式，大小不超过100MB</div>
        </el-upload>
      </el-form>
      <div slot="footer">
        <el-button @click="homeworkDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveHomework">确定</el-button>
      </div>
    </el-dialog>

    <!-- 作业提交列表对话框 -->
    <el-dialog
        title="作业提交列表"
        :visible.sync="submissionDialog.visible"
        width="70%"
    >
      <el-table :data="submissionList" border>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="studentName" label="姓名" width="120" />
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column prop="content" label="作业内容" show-overflow-tooltip />
        <el-table-column prop="score" label="得分" width="80" />
        <el-table-column label="操作" width="200" fixed="right">
          <template slot-scope="scope">
            <el-button
                v-if="scope.row.attachmentPath"
                size="mini"
                type="info"
                @click="downloadAttachment(scope.row.attachmentPath)"
            >下载作业</el-button>
            <el-button
                size="mini"
                type="primary"
                @click="openGradeDialog(scope.row)"
            >评分</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 评分对话框 -->
    <el-dialog
        title="作业评分"
        :visible.sync="gradeDialog.visible"
        width="40%"
    >
      <el-form ref="gradeForm" :model="gradeForm" :rules="gradeRules" label-width="80px">
        <el-form-item label="得分" prop="score">
          <el-input-number v-model="gradeForm.score" :min="0" :max="100" />
        </el-form-item>
        <el-form-item label="评语" prop="feedback">
          <el-input
              type="textarea"
              v-model="gradeForm.feedback"
              :rows="4"
              placeholder="请输入评语"
          />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="gradeDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="submitGrade">确定</el-button>
      </div>
    </el-dialog>

    <!-- 成绩统计对话框 -->
    <el-dialog
        title="成绩统计"
        :visible.sync="statsDialog.visible"
        width="50%"
    >
      <el-descriptions :column="2" border>
        <el-descriptions-item label="平均分">{{ stats.avgScore }}</el-descriptions-item>
        <el-descriptions-item label="最高分">{{ stats.maxScore }}</el-descriptions-item>
        <el-descriptions-item label="最低分">{{ stats.minScore }}</el-descriptions-item>
        <el-descriptions-item label="已批改人数">{{ stats.gradedCount }}</el-descriptions-item>
        <el-descriptions-item label="提交人数">{{ stats.totalSubmissions }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'HomeworkManagement',
  data() {
    return {
      pickerOptions: {
        disabledDate(time) {
          // 禁用今天之前的日期
          return time.getTime() < Date.now() - 8.64e7; // 86400000 = 24 * 60 * 60 * 1000
        }
      },
      // 教师信息
      teacherInfo: {
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
      // 作业对话框
      homeworkDialog: {
        visible: false,
        title: '布置作业'
      },
      homeworkForm: {
        courseId: null, // 使用 null 而不是空字符串
        title: '',
        description: '',
        deadline: '',
        totalScore: 100,
        attachmentPath: '',
        publisherId: ''
      },

      // 作业表单验证规则
      homeworkRules: {
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        title: [{ required: true, message: '请输入作业标题', trigger: 'blur' }],
        description: [{ required: true, message: '请输入作业描述', trigger: 'blur' }],
        deadline: [{ required: true, message: '请选择截止日期', trigger: 'change' }],
        totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }]
      },
      // 提交列表对话框
      submissionDialog: {
        visible: false,
        homeworkId: null
      },
      // 提交列表
      submissionList: [],
      // 评分对话框
      gradeDialog: {
        visible: false
      },
      // 评分表单
      gradeForm: {
        submissionId: '',
        score: null,
        feedback: ''
      },
      // 评分表单验证规则
      gradeRules: {
        score: [{ required: true, message: '请输入分数', trigger: 'blur' }]
      },
      // 统计对话框
      statsDialog: {
        visible: false
      },
      // 统计数据
      stats: {
        avgScore: 0,
        maxScore: 0,
        minScore: 0,
        gradedCount: 0,
        totalSubmissions: 0
      },

    }
  },

  created() {
    const curUser = this.getCurrentUser()
    if (curUser) {
      this.teacherInfo = curUser
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
        // 先从sessionStorage获取用户信息
        const curUser = this.getCurrentUser()
        console.log("当前用户",curUser.id)
        if (!curUser || !curUser.id) {
          this.$message.error('未获取到用户信息，请重新登录')
          return
        }

        // 使用用户ID查询最新信息
        const res = await this.$axios.post(this.$httpUrl + '/user/listPageC1', {
          pageSize: this.pageSize,
          pageNum: this.pageNum,
          param: {
            id: curUser.id
          }
        })

        if (res.data.code == 200 && res.data.data.length > 0) {
          this.teacherInfo = res.data.data[0]

          // 更新sessionStorage中的用户信息
          curUser.name = this.teacherInfo.name
          curUser.phone = this.teacherInfo.phone
          sessionStorage.setItem("CurUser", JSON.stringify(curUser))
        } else {
          // 如果接口查询失败，直接使用sessionStorage中的信息
          this.teacherInfo = curUser
        }
      } catch (error) {
        console.error('获取用户信息失败：', error)
        // 如果接口调用失败，使用sessionStorage中的信息
        const curUser = this.getCurrentUser()
        if (curUser) {
          this.teacherInfo = curUser
        } else {
          this.$message.error('获取用户信息失败，请重新登录')
        }
      }
    },

    // 加载课程列表
    async loadCourseList() {
      try {
        const res = await this.$axios.get(this.$httpUrl + '/course/teacher/courses/' + this.teacherInfo.id)
        if (res.data.code === 200) {
          this.courseList = res.data.data
        } else {
          this.$message.error('获取课程列表失败')
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
            courseId: this.searchForm.courseId ? Number(this.searchForm.courseId) : null // 转换为数字类型
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

    // 打开布置作业对话框
    openHomeworkDialog() {
      this.homeworkDialog.visible = true
      this.homeworkForm = {
        courseId: '',
        title: '',
        description: '',
        deadline: '',
        totalScore: 100,
        attachmentPath: '',
        publisherId: this.teacherInfo.id
      }
    },

    handleUploadError(err) {
      console.error('Upload error:', err);
      this.$message.error('文件上传失败，请重试');
      // 清除失败的文件
      this.$refs.upload.clearFiles();
    },

// 在保存作业成功后也清除文件列表
    async saveHomework() {
      try {
        const valid = await this.$refs.homeworkForm.validate()
        if (!valid) return

        const formData = {
          ...this.homeworkForm,
          courseId: this.homeworkForm.courseId ? parseInt(this.homeworkForm.courseId) : null,
          totalScore: this.homeworkForm.totalScore ? parseInt(this.homeworkForm.totalScore) : null
        }

        console.log('Sending homework data:', formData)

        const res = await this.$axios.post(this.$httpUrl + '/homework/assign', formData)
        if (res.data.code === 200) {
          this.$message.success('作业布置成功')
          this.homeworkDialog.visible = false
          this.loadHomeworkList()
          // 清除文件列表
          this.$refs.upload.clearFiles();
        } else {
          this.$message.error(res.data.msg || '作业布置失败')
        }
      } catch (error) {
        console.error('保存作业失败:', error)
        this.$message.error(error.response?.data?.msg || '作业布置失败')
      }
    },

    // 查看提交列表
    async viewSubmissions(homeworkId) {
      try {
        const params = {
          pageNum: 1,
          pageSize: 100,
          param: {
            homeworkId: homeworkId
          }
        }
        const res = await this.$axios.post(this.$httpUrl + '/homework/submissions', params)
        if (res.data.code === 200) {
          this.submissionList = res.data.data
          this.submissionDialog.visible = true
        }
      } catch (error) {
        console.error('获取提交列表失败:', error)
        this.$message.error('获取提交列表失败')
      }
    },

    // 打开评分对话框
    openGradeDialog(submission) {
      this.gradeForm = {
        submissionId: submission.submissionId,
        score: submission.score,
        feedback: submission.feedback || ''
      }
      this.gradeDialog.visible = true
    },

    // 提交评分
    async submitGrade() {
      try {
        const valid = await this.$refs.gradeForm.validate()
        if (!valid) return

        const res = await this.$axios.post(this.$httpUrl + '/homework/grade', this.gradeForm)
        if (res.data.code === 200) {
          this.$message.success('评分成功')
          this.gradeDialog.visible = false
          this.viewSubmissions(this.submissionDialog.homeworkId)
        }
      } catch (error) {
        console.error('提交评分失败:', error)
        this.$message.error('评分失败')
      }
    },

    // 查看统计
    async viewStats(homeworkId) {
      try {
        const res = await this.$axios.get(this.$httpUrl + `/homework/stats/${homeworkId}`)
        if (res.data.code === 200) {
          this.stats = res.data.data
          this.statsDialog.visible = true
        }
      } catch (error) {
        console.error('获取统计信息失败:', error)
        this.$message.error('获取统计信息失败')
      }
    },
    handleUploadSuccess(response, file) {
      console.log('Upload success response:', response); // 添加调试日志
      if (response.code === 200) {
        this.homeworkForm.attachmentPath = response.data;
        this.$message.success('文件上传成功');
      } else {
        this.$message.error(response.msg || '文件上传失败');
      }
    },



    beforeUpload(file) {
      // 验证文件类型
      const validTypes = [
        'application/pdf',
        'application/msword',
        'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
        'application/vnd.ms-powerpoint',
        'application/vnd.openxmlformats-officedocument.presentationml.presentation',
        'application/zip',
        'application/x-rar-compressed',
        'text/plain'
      ]
      const isValidType = validTypes.includes(file.type)
      const isLt100M = file.size / 1024 / 1024 < 100

      if (!isValidType) {
        this.$message.error('不支持的文件格式!')
        return false
      }
      if (!isLt100M) {
        this.$message.error('文件大小不能超过100MB!')
        return false
      }
      return true
    },


    async downloadAttachment(path) {
      try {
        // 直接将路径作为参数传入
        const response = await this.$axios.get(this.$httpUrl + '/homework/download/' + encodeURIComponent(path), {
          responseType: 'blob'
        });

        const contentDisposition = response.headers['content-disposition'];
        let fileName = 'homework';
        if (contentDisposition) {
          const matches = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/);
          if (matches && matches[1]) {
            fileName = decodeURIComponent(matches[1].replace(/['"]/g, ''));
          }
        }

        const blob = new Blob([response.data]);
        const link = document.createElement('a');
        link.href = window.URL.createObjectURL(blob);
        link.download = fileName;
        document.body.appendChild(link); // 添加到 document
        link.click();
        document.body.removeChild(link); // 清理 DOM
        window.URL.revokeObjectURL(link.href);

        this.$message.success('下载成功');
      } catch (error) {
        console.error('下载失败:', error);
        this.$message.error('下载失败');
      }
    },


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
</style>