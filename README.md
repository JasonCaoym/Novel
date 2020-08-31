## 开发环境

- 开发工具: Android Studio 3.2+
- JAVA版本: JDK 1.8+

## GIT配置

### 个人信息配置

- 用户名配置 `git config --global user.name "xxx"`
- 邮箱配置 `git config --global user.email xxx@chinamobiad.com`

### 对比工具配置

> 以Beyond Compare 4为例

- git config --global diff.tool bc3
- git config --global difftool.bc3.path "C:/Program Files/Beyond Compare 4/bcomp.exe"
- git config --global merge.tool bc3
- git config --global mergetool.bc3.path "C:/Program Files/Beyond Compare 4/bcomp.exe"

### 流程规范

1. 提交代码之前先执行 `git fetch`
2. 服务器有更新时, 进入流程3; 否则, 结束
3. 本地无需提交代码时, 执行`git merge <仓库名>/<分支名>`, 结束; 否则执行流程4
4. 本地需要提交代码时, 执行`git rebase -i <仓库名>/<分支名>`
5. 执行`git push`

### 日志规范

- 提交功能代码时, "功能: <任务ID><空格> <任务标题><换行><描述>", 描述可以为空, 多个任务时, 以换行区分
- 提交修复代码时, "BUG: <BUG ID><空格><BUG标题><换行><描述>", 描述可以为空, 多个BUG时, 以换行区分