<template>
  <div class="exam-management">
    <!-- 搜索和操作栏 -->
    <div class="search-bar">
      <el-select v-model="searchForm.courseId" placeholder="请选择课程" style="width: 200px">
        <el-option
            v-for="course in courseList"
            :key="course.courseId"
            :label="course.courseName"
            :value="course.courseId"
        />
      </el-select>
      <el-button type="primary" @click="loadExamList" style="margin-left: 10px">查询</el-button>
      <el-button type="primary" @click="openExamDialog()" style="margin-left: 10px">创建考试</el-button>
    </div>

    <!-- 考试列表 -->
    <el-table :data="examList" border style="width: 100%; margin-top: 20px">
      <el-table-column prop="examId" label="考试ID" width="100" />
      <el-table-column prop="title" label="考试标题" width="180" />
      <el-table-column prop="description" label="考试说明" show-overflow-tooltip />
      <el-table-column prop="examDate" label="考试日期" width="120" />
      <el-table-column prop="startTime" label="开始时间" width="100" />
      <el-table-column prop="endTime" label="结束时间" width="100" />
      <el-table-column prop="totalScore" label="总分" width="80" />
      <el-table-column label="操作" width="380" fixed="right">
        <template slot-scope="scope">
          <el-button
              size="mini"
              type="primary"
              @click="viewArrangements(scope.row.examId)"
          >考场安排</el-button>
          <el-button
              size="mini"
              type="success"
              @click="uploadPaper(scope.row.examId)"
          >上传试卷</el-button>
          <el-button
              size="mini"
              type="warning"
              @click="processAnswers(scope.row.examId)"
          >批改试卷</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 创建考试对话框 -->
    <el-dialog
        :title="examDialog.title"
        :visible.sync="examDialog.visible"
        width="50%"
    >
      <el-form
          ref="examForm"
          :model="examForm"
          :rules="examRules"
          label-width="100px"
      >
        <el-form-item label="课程" prop="courseId">
          <el-select v-model="examForm.courseId" placeholder="请选择课程">
            <el-option
                v-for="course in courseList"
                :key="course.courseId"
                :label="course.courseName"
                :value="course.courseId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="考试标题" prop="title">
          <el-input v-model="examForm.title" />
        </el-form-item>
        <el-form-item label="考试说明" prop="description">
          <el-input
              type="textarea"
              v-model="examForm.description"
              :rows="4"
          />
        </el-form-item>
        <el-form-item label="考试日期" prop="examDate">
          <el-date-picker
              v-model="examForm.examDate"
              type="date"
              placeholder="选择考试日期"
              value-format="yyyy-MM-dd"
              :picker-options="pickerOptions"
          />
        </el-form-item>
        <el-form-item label="考试时间" required>
          <el-col :span="11">
            <el-form-item prop="startTime">
              <el-time-picker
                  v-model="examForm.startTime"
                  placeholder="开始时间"
                  value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="2" class="text-center">-</el-col>
          <el-col :span="11">
            <el-form-item prop="endTime">
              <el-time-picker
                  v-model="examForm.endTime"
                  placeholder="结束时间"
                  value-format="HH:mm:ss"
              />
            </el-form-item>
          </el-col>
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input-number v-model="examForm.totalScore" :min="0" :max="100" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="examDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveExam">确定</el-button>
      </div>
    </el-dialog>

    <!-- 考场安排对话框 -->
    <el-dialog
        title="考场安排"
        :visible.sync="arrangementDialog.visible"
        width="70%"
    >
      <div class="arrangement-controls">
        <el-select v-model="arrangementForm.classroomId" placeholder="选择教室">
          <el-option
              v-for="room in classroomList"
              :key="room.classroomId"
              :label="room.buildingName + room.roomNumber"
              :value="room.classroomId"
          />
        </el-select>
        <el-radio-group v-model="arrangementForm.isRandomSeating" style="margin-left: 20px">
          <el-radio :label="1">随机座位</el-radio>
          <el-radio :label="0">顺序座位</el-radio>
        </el-radio-group>
        <el-button type="primary" @click="createArrangement" style="margin-left: 20px">
          创建考场
        </el-button>
      </div>
      <el-table :data="arrangementList" border style="margin-top: 20px">
        <el-table-column prop="arrangementId" label="安排ID" width="100"/>
        <el-table-column label="教室" width="150">
          <template slot-scope="scope">
            {{ scope.row.buildingName }} {{ scope.row.roomNumber }}
          </template>
        </el-table-column>
        <el-table-column prop="invigilatorName" label="监考教师" width="120">
          <template slot-scope="scope">
            {{ scope.row.invigilatorName || '未指定' }}
          </template>
        </el-table-column>
        <el-table-column label="座位安排" width="100">
          <template slot-scope="scope">
            {{ scope.row.isRandomSeating === 1 ? '随机' : '顺序' }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="primary"
                @click="handleViewSeating(scope.row)">
              查看座位
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- 座位查看对话框 -->
    <el-dialog
        title="座位安排详情"
        :visible.sync="seatingDialog.visible"
        width="70%">
      <div class="seating-info" v-if="seatingDialog.data">
        <div class="info-header">
          <span>教室：{{ seatingDialog.arrangement.buildingName }} {{ seatingDialog.arrangement.roomNumber }}</span>
          <span>监考教师：{{ seatingDialog.arrangement.invigilatorName || '未指定' }}</span>
        </div>
        <el-table
            :data="seatingDialog.data"
            border
            style="margin-top: 20px">
          <el-table-column prop="seatRow" label="座位行" width="100"/>
          <el-table-column prop="seatColumn" label="座位列" width="100"/>
          <el-table-column prop="studentId" label="学号" width="120"/>
          <el-table-column prop="studentName" label="姓名"/>
          <el-table-column prop="questionVersion" label="试题版本" width="100"/>
        </el-table>
      </div>
    </el-dialog>

    <!-- 修改上传试卷对话框部分 -->
    <el-dialog
        title="上传试卷"
        :visible.sync="paperDialog.visible"
        width="50%">
      <el-form ref="uploadForm" :model="uploadForm">
        <!-- 试卷模板上传 -->
        <el-form-item label="试卷模板">
          <el-upload
              class="paper-upload"
              :action="uploadUrl + '/exam/paper/upload'"
              :data="{examId: uploadForm.examId}"
              :headers="headers"
              :before-upload="beforePaperUpload"
              :on-success="handlePaperUploadSuccess"
              :on-error="handleUploadError"
              :multiple="false"
              name="file">
            <el-button size="small" type="primary">上传试卷模板</el-button>
            <div slot="tip" class="el-upload__tip">支持PDF格式，大小不超过100MB</div>
          </el-upload>
        </el-form-item>

        <!-- 答题卡模板上传 -->
        <el-form-item label="答题卡模板">
          <el-upload
              class="answer-upload"
              :action="uploadUrl + '/exam/answer/upload'"
          :data="{examId: uploadForm.examId}"
          :headers="headers"
          :before-upload="beforeAnswerUpload"
          :on-success="handleAnswerUploadSuccess"
          :on-error="handleUploadError"
          :multiple="false"
          name="file">
          <el-button size="small" type="primary">上传答题卡模板</el-button>
          <div slot="tip" class="el-upload__tip">支持PDF格式，大小不超过100MB</div>
          </el-upload>
        </el-form-item>
      </el-form>

      <!-- OCR配置部分保持不变 -->
      <el-form ref="ocrForm" :model="ocrConfig" label-width="100px">
        <el-form-item label="OCR配置">
          <el-input
              type="textarea"
              v-model="ocrConfig.config"
              :rows="4"
              placeholder="请输入OCR配置JSON"
          />
        </el-form-item>
      </el-form>

      <div slot="footer">
        <el-button @click="paperDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveOCRConfig">保存配置</el-button>
      </div>
    </el-dialog>

    <!-- 试卷批改对话框 -->
    <el-dialog
        title="试卷批改"
        :visible.sync="gradingDialog.visible"
        width="70%"
    >
      <el-table :data="answerList" border>
        <el-table-column prop="studentId" label="学号" width="120" />
        <el-table-column prop="uploadTime" label="上传时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template slot-scope="scope">
            <el-tag :type="getAnswerStatusType(scope.row.status)">
              {{ getAnswerStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="totalScore" label="总分" width="80" />
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button
                size="mini"
                type="primary"
                @click="processAnswer(scope.row)"
                :disabled="scope.row.status === 2"
            >OCR识别</el-button>
            <el-button
                size="mini"
                type="success"
                @click="autoGrade(scope.row)"
                :disabled="scope.row.status !== 1"
            >自动评分</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: 'ExamManagement',
  data() {
    return {

      pickerOptions: {
        disabledDate(time) {
          // 禁用今天之前的日期
          return time.getTime() < Date.now() - 8.64e7; // 86400000 = 24 * 60 * 60 * 1000
        }
      },

      uploadUrl: this.$httpUrl,
      uploadForm: {
        examId: null,
      },
      headers: {

      },
      // 座位查看对话框数据
      seatingDialog: {
        visible: false,
        data: null,
        arrangement: null
      },

      // 教师信息
      teacherInfo: {
        id: '',
        name: ''
      },
      // 搜索表单
      searchForm: {
        courseId: ''
      },
      // 课程列表
      courseList: [],
      // 考试列表
      examList: [],
      // 教室列表
      classroomList: [],
      // 考试对话框
      examDialog: {
        visible: false,
        title: '创建考试'
      },
      // 考试表单
      examForm: {
        courseId: '',
        title: '',
        description: '',
        examDate: '',
        startTime: '',
        endTime: '',
        totalScore: 100,
        teacherId: ''
      },
      // 考试表单验证规则
      examRules: {
        courseId: [{ required: true, message: '请选择课程', trigger: 'change' }],
        title: [{ required: true, message: '请输入考试标题', trigger: 'blur' }],
        examDate: [{ required: true, message: '请选择考试日期', trigger: 'change' }],
        startTime: [{ required: true, message: '请选择开始时间', trigger: 'change' }],
        endTime: [{ required: true, message: '请选择结束时间', trigger: 'change' }],
        totalScore: [{ required: true, message: '请输入总分', trigger: 'blur' }]
      },
      // 考场安排对话框
      arrangementDialog: {
        visible: false,
        examId: null
      },
      // 考场安排表单
      arrangementForm: {
        examId: null,
        classroomId: '',
        isRandomSeating: 0,
        invigilatorId: ''
      },
      // 考场安排列表
      arrangementList: [],
      // 试卷上传对话框
      paperDialog: {
        visible: false,
        examId: null
      },
      // OCR配置
      ocrConfig: {
        config: ''
      },
      // 上传参数
      uploadData: {
        examId: null
      },
      // 批改对话框
      gradingDialog: {
        visible: false,
        examId: null
      },
      // 答卷列表
      answerList: []
    }

  },

  created() {
    this.loadUserInfo()
    this.loadCourseList()
    this.loadExamList()
    this.loadClassrooms()
  },

  methods: {
    // 上传试卷
    uploadPaper(examId) {
      this.paperDialog.visible = true
      this.paperDialog.examId = examId
      this.uploadForm.examId = examId  // 设置考试ID
    },

    // 上传文件前的验证
    beforePaperUpload(file) {
      const isPDF = file.type === 'application/pdf'
      const isLt100M = file.size / 1024 / 1024 < 100

      if (!isPDF) {
        this.$message.error('只能上传PDF格式的文件!')
        return false
      }
      if (!isLt100M) {
        this.$message.error('文件大小不能超过100MB!')
        return false
      }
      return true
    },

    // 处理试卷上传成功
    handlePaperUploadSuccess(response) {
      if (response.code === 200) {
        this.$message.success('试卷模板上传成功')
        // 保存试卷ID用于后续OCR配置
        this.paperId = response.data.paperId
      } else {
        this.$message.error(response.msg || '试卷模板上传失败')
      }
    },



    // 处理上传错误
    handleUploadError(err) {
      console.error('上传失败:', err)
      this.$message.error('上传失败，请检查文件格式和大小')
    },




    // 保存OCR配置
    async saveOCRConfig() {
      if (!this.paperId) {
        this.$message.warning('请先上传试卷模板')
        return
      }

      try {
        const res = await this.$axios.post(
            this.$httpUrl + `/exam/paper/${this.paperId}/ocr`,
            this.ocrConfig.config
        )
        if (res.data.code === 200) {
          this.$message.success('OCR配置保存成功')
          this.paperDialog.visible = false
        } else {
          this.$message.error(res.data.msg || 'OCR配置保存失败')
        }
      } catch (error) {
        console.error('保存OCR配置失败:', error)
        this.$message.error('OCR配置保存失败')
      }
    },
    // 获取状态类型
    getStatusType(status) {
      const types = {
        0: 'info',
        1: 'success',
        2: 'danger'
      }
      return types[status] || 'info'
    },

    // 获取状态文本
    getStatusText(status) {
      const texts = {
        0: '未开始',
        1: '进行中',
        2: '已结束'
      }
      return texts[status] || '未知'
    },

    // 加载用户信息
    loadUserInfo() {
      const userStr = sessionStorage.getItem("CurUser")
      if (userStr) {
        this.teacherInfo = JSON.parse(userStr)
      }
    },
    beforeAnswerUpload(file) {
      const isPDF = file.type === 'application/pdf'
      const isLt100M = file.size / 1024 / 1024 < 100

      if (!isPDF) {
        this.$message.error('只能上传PDF格式的文件!')
      }
      if (!isLt100M) {
        this.$message.error('文件大小不能超过100MB!')
      }
      return isPDF && isLt100M
    },

    handleAnswerUploadSuccess(response) {
      if (response.code === 200) {
        this.$message.success('答题卡上传成功')
      } else {
        this.$message.error(response.msg || '答题卡上传失败')
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

    // 加载考试列表
    async loadExamList() {
      try {
        const params = {
          courseId: this.searchForm.courseId,
          teacherId: this.teacherInfo.id
        }
        const res = await this.$axios.get(this.$httpUrl + '/exam/teacher/' + this.teacherInfo.id, params)
        if (res.data.code === 200) {
          this.examList = res.data.data
        } else {
          this.$message.error('获取考试列表失败')
        }
      } catch (error) {
        console.error('加载考试列表失败:', error)
        this.$message.error('获取考试列表失败')
      }
    },

    // 加载教室列表
    async loadClassrooms() {
      try {
        const res = await this.$axios.get(this.$httpUrl + '/exam/classroom/available')
        if (res.data.code === 200) {
          this.classroomList = res.data.data.filter(room => room.buildingName === '逸夫楼')
        } else {
          this.$message.error('获取教室列表失败')
        }
      } catch (error) {
        console.error('加载教室列表失败:', error)
        this.$message.error('获取教室列表失败')
      }
    },

    // 打开创建考试对话框
    openExamDialog() {
      this.examDialog.visible = true
      this.examForm = {
        courseId: '',
        title: '',
        description: '',
        examDate: '',
        startTime: '',
        endTime: '',
        totalScore: 100,
        teacherId: this.teacherInfo.id
      }
    },

    // 保存考试
    async saveExam() {
      try {
        const valid = await this.$refs.examForm.validate()
        if (!valid) return

        const res = await this.$axios.post(this.$httpUrl + '/exam/create', this.examForm)
        if (res.data.code === 200) {
          this.$message.success('考试创建成功')
          this.examDialog.visible = false
          this.loadExamList()
        } else {
          this.$message.error(res.data.msg || '考试创建失败')
        }
      } catch (error) {
        console.error('保存考试失败:', error)
        this.$message.error('考试创建失败')
      }
    },


    // 查看考场安排
    async viewArrangements(examId) {
      this.arrangementDialog.visible = true;
      this.arrangementDialog.examId = examId;
      this.arrangementForm.examId = examId;

      try {
        const res = await this.$axios.get(this.$httpUrl + '/exam/arrangement/' + examId);
        console.log('考场安排响应:', res.data); // 添加调试日志

        if (res.data.code === 200) {
          this.arrangementList = res.data.data;
        } else {
          this.$message.error(res.data.msg || '获取考场安排失败');
        }
      } catch (error) {
        console.error('查看考场安排失败:', error);
        this.$message.error('获取考场安排失败');
      }
    },

    // 创建考场
    async createArrangement() {
      if (!this.arrangementForm.classroomId) {
        this.$message.warning('请选择教室');
        return;
      }

      try {
        const examInfo = this.examList.find(exam => exam.examId === this.arrangementDialog.examId);
        if (!examInfo) {
          this.$message.error('获取考试信息失败');
          return;
        }

        console.log('创建考场参数:', {
          examId: this.arrangementDialog.examId,
          classroomId: this.arrangementForm.classroomId,
          isRandomSeating: this.arrangementForm.isRandomSeating,
          invigilatorId: this.teacherInfo.id
        });

        const res = await this.$axios.post(this.$httpUrl + '/exam/arrangement/create', {
          examId: this.arrangementDialog.examId,
          classroomId: this.arrangementForm.classroomId,
          isRandomSeating: this.arrangementForm.isRandomSeating,
          invigilatorId: this.teacherInfo.id
        });

        if (res.data.code === 200) {
          this.$message.success('考场安排创建成功');
          await this.viewArrangements(this.arrangementDialog.examId);
        } else {
          this.$message.error(res.data.msg || '考场安排创建失败');
        }
      } catch (error) {
        console.error('创建考场安排失败:', error);
        this.$message.error(error.response?.data?.msg || '考场安排创建失败');
      }
    },

// 查看座位
    async handleViewSeating(arrangement) {
      if (!arrangement || !arrangement.arrangementId) {
        this.$message.warning('无效的考场安排');
        return;
      }

      try {
        console.log('查看座位安排:', arrangement);
        // 调用后端生成座位的接口
        const examInfo = this.examList.find(exam => exam.examId === this.arrangementDialog.examId);
        if (!examInfo) {
          this.$message.error('获取考试信息失败');
          return;
        }

        // 这里应该先获取该课程的学生列表，然后根据是否随机座位来生成座位安排
        const studentListRes = await this.$axios.get(this.$httpUrl + `/course/students/${examInfo.courseId}`);
        if (studentListRes.data.code !== 200) {
          this.$message.error('获取学生列表失败');
          return;
        }

        const studentIds = studentListRes.data.data.map(student => student.id);
        const generateRes = await this.$axios.post(
            this.$httpUrl + `/exam/arrangement/${arrangement.arrangementId}/${arrangement.isRandomSeating ? 'random' : 'ordered'}`,
            studentIds
        );

        if (generateRes.data.code !== 200) {
          this.$message.error('生成座位安排失败');
          return;
        }

        // 获取座位安排信息
        const res = await this.$axios.get(
            this.$httpUrl + `/exam/seating/${arrangement.arrangementId}`
        );

        if (res.data.code === 200) {
          this.seatingDialog = {
            visible: true,
            data: res.data.data,
            arrangement: arrangement
          };
        } else {
          this.$message.error(res.data.msg || '获取座位信息失败');
        }
      } catch (error) {
        console.error('查看座位安排失败:', error);
        this.$message.error('获取座位信息失败');
      }
    },





    // 处理答卷
    async processAnswers(examId) {
      this.gradingDialog.visible = true
      this.gradingDialog.examId = examId
      try {
        const res = await this.$axios.get(this.$httpUrl + `/exam/answer/list/${examId}`)
        if (res.data.code === 200) {
          this.answerList = res.data.data
        } else {
          this.$message.error('获取答卷列表失败')
        }
      } catch (error) {
        console.error('获取答卷列表失败:', error)
        this.$message.error('获取答卷列表失败')
      }
    },

    // OCR识别处理
    async processAnswer(answer) {
      try {
        const res = await this.$axios.post(this.$httpUrl + `/exam/answer/${answer.answerId}/process`)
        if (res.data.code === 200) {
          this.$message.success('OCR识别成功')
          // 重新加载答卷列表
          this.processAnswers(this.gradingDialog.examId)
        } else {
          this.$message.error(res.data.msg || 'OCR识别失败')
        }
      } catch (error) {
        console.error('OCR识别失败:', error)
        this.$message.error('OCR识别失败')
      }
    },

    // 自动评分
    async autoGrade(answer) {
      try {
        const res = await this.$axios.post(this.$httpUrl + `/exam/answer/${answer.answerId}/grade`)
        if (res.data.code === 200) {
          this.$message.success('自动评分完成')
          // 重新加载答卷列表
          this.processAnswers(this.gradingDialog.examId)
        } else {
          this.$message.error(res.data.msg || '自动评分失败')
        }
      } catch (error) {
        console.error('自动评分失败:', error)
        this.$message.error('自动评分失败')
      }
    },

    // 获取答卷状态类型
    getAnswerStatusType(status) {
      const types = {
        0: 'info',     // 待批改
        1: 'warning',  // 批改中
        2: 'success'   // 已批改
      }
      return types[status] || 'info'
    },

    // 获取答卷状态文本
    getAnswerStatusText(status) {
      const texts = {
        0: '待批改',
        1: '批改中',
        2: '已批改'
      }
      return texts[status] || '未知'
    }
  }
}
</script>

<style scoped>
.exam-management {
  padding: 20px;
}

.search-bar {
  margin-bottom: 20px;
  display: flex;
  align-items: center;
}

.arrangement-controls {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.paper-upload, .answer-upload {
  margin-bottom: 20px;
}

.el-upload__tip {
  line-height: 1.2;
  margin-top: 5px;
  color: #909399;
}

.text-center {
  text-align: center;
  line-height: 40px;
}
</style>