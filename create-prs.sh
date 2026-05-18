#!/bin/bash
# Script to create 20 sequential PRs for demo001-demo020 following commit guidelines
# Each PR: branch feature/demo00N, commit with proper type, push, create PR, merge to main

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo -e "${GREEN}=== 开始创建 demo001 到 demo020 的 20 个 PR ===${NC}"

# Ensure we are on latest main
git checkout main
git pull --ff-only origin main || true

# Define messages for each demo (type: subject\nbody)
# Using the commit message guidelines provided (feat, refactor, docs, chore etc.)

declare -A DEMO_MESSAGES
declare -A DEMO_BODIES

DEMO_MESSAGES[1]="feat: 添加 demo001 最原始的 JDBC 实现"
DEMO_BODIES[1]="所有 CRUD 逻辑写在一个类中，使用原生 JDBC + Scanner 实现控制台菜单。目的是彻底看清楚 Connection、PreparedStatement、ResultSet 的底层工作原理。"

DEMO_MESSAGES[2]="feat: 添加 demo002 传统分层架构"
DEMO_BODIES[2]="引入 Maven，拆分为 Entity + Mapper + Service + Controller 层，所有 getter/setter 手动编写。理解分层思想和手动装配的麻烦。"

DEMO_MESSAGES[3]="feat: 添加 demo003 引入 Lombok 简化 Entity"
DEMO_BODIES[3]="仅在 Entity 上使用 Lombok（@Data + @Builder 等）。展示只改一个文件就能大幅减少样板代码。"

DEMO_MESSAGES[4]="feat: 添加 demo004 加入日志 @Slf4j"
DEMO_BODIES[4]="在 demo003 基础上增加日志能力，使用 @Slf4j 注解。体现已有 Lombok 的项目上增加新功能的渐进方式。"

DEMO_MESSAGES[5]="feat: 添加 demo005 MyBatis XML 替换手写 JDBC"
DEMO_BODIES[5]="用 MyBatis XML 完全替代手写的 JDBC MapperImpl。第一次接触 ORM，SQL 写在 XML 中。"

DEMO_MESSAGES[6]="feat: 添加 demo006 MyBatis 注解方式"
DEMO_BODIES[6]="把 XML 中的 SQL 迁移到接口上的 @Select、@Insert 等注解。对比 MyBatis 的两种主流写法。"

DEMO_MESSAGES[7]="feat: 添加 demo007 MyBatis-Spring 集成"
DEMO_BODIES[7]="引入 Spring + MyBatis-Spring，彻底告别手动 SqlSession 的写法。进入依赖注入时代。"

DEMO_MESSAGES[8]="feat: 添加 demo008 Spring Boot 最终版"
DEMO_BODIES[8]="使用 Spring Boot 自动配置，抛弃 applicationContext.xml，改用 application.yml。掌握现代 Spring Boot 项目标准结构。"

DEMO_MESSAGES[9]="feat: 添加 demo009 MyBatis-Plus BaseMapper"
DEMO_BODIES[9]="Mapper 直接继承 BaseMapper<Student>，不再手写任何 CRUD 方法。感受 MyBatis-Plus 对 Mapper 层的极致简化。"

DEMO_MESSAGES[10]="feat: 添加 demo010 MyBatis-Plus Service 封装"
DEMO_BODIES[10]="Service 层也继承 ServiceImpl，进一步减少业务代码。MyBatis-Plus 在 Service 层的威力。"

DEMO_MESSAGES[11]="feat: 添加 demo011 统一返回结果 + 全局异常处理"
DEMO_BODIES[11]="引入 Result<T> 统一响应格式 + GlobalExceptionHandler 集中处理异常。建立生产级项目的响应和异常规范。"

DEMO_MESSAGES[12]="feat: 添加 demo012 RESTful API 改造"
DEMO_BODIES[12]="彻底移除控制台菜单逻辑，改用 @RestController 提供 HTTP 接口。从控制台程序转型为真正的 Web API 项目。"

DEMO_MESSAGES[13]="feat: 添加 demo013 参数校验 Validation"
DEMO_BODIES[13]="引入 DTO + @Valid + 校验注解，增强 GlobalExceptionHandler 处理校验错误。让接口参数校验自动化、规范化。"

DEMO_MESSAGES[14]="feat: 添加 demo014 分页 + 条件查询"
DEMO_BODIES[14]="使用 MyBatis-Plus 的 Page 和 LambdaQueryWrapper 实现分页和动态搜索。掌握真实项目中最常用的列表查询方式。"

DEMO_MESSAGES[15]="feat: 添加 demo015 事务管理 @Transactional"
DEMO_BODIES[15]="新增批量新增接口，使用 @Transactional 演示事务回滚机制。理解如何保证数据一致性。"

DEMO_MESSAGES[16]="feat: 添加 demo016 统一日志 + 请求响应日志"
DEMO_BODIES[16]="引入 LoggingFilter + MDC + 完整 Request/Response Body 记录 + 敏感信息脱敏。让系统具备生产环境最基础的可观测性。"

DEMO_MESSAGES[17]="feat: 添加 demo017 Redis 缓存集成"
DEMO_BODIES[17]="集成 spring-boot-starter-data-redis，自定义 RedisTemplate，使用 @Cacheable + 手动缓存穿透防护。掌握缓存最佳实践。"

DEMO_MESSAGES[18]="feat: 添加 demo018 JWT + Spring Security 基础认证"
DEMO_BODIES[18]="引入 Spring Security 6 + java-jwt，实现 /auth/login 签发 JWT + JwtAuthenticationFilter。基于 sys_user 表做真实用户认证。"

DEMO_MESSAGES[19]="feat: 添加 demo019 Docker + Docker Compose 一键部署"
DEMO_BODIES[19]="多阶段 Dockerfile + 完整 docker-compose.yml（app + mysql + redis）+ 健康检查。项目具备开箱即用的部署能力。"

DEMO_MESSAGES[20]="refactor: 添加 demo020 完整项目重构"
DEMO_BODIES[20]="DTO/VO 彻底分离 + 目录模块化 + 代码规范 + 转换逻辑收敛。从「能跑」进化到「好维护、好扩展」。为后续电商系统做好架构准备。"

for i in $(seq 1 20); do
  DEMO_NUM=$(printf "%03d" $i)
  BRANCH="feature/demo${DEMO_NUM}"
  DEMO_DIR="demo${DEMO_NUM}"

  echo -e "\n${YELLOW}>>> 处理 ${DEMO_NUM} : ${DEMO_MESSAGES[$i]} <<<${NC}"

  # Create branch from latest main
  git checkout main
  git pull --ff-only origin main
  git checkout -b "${BRANCH}"

  # Add only the demo dir (target/ ignored via .gitignore)
  git add "${DEMO_DIR}/"

  # Check if anything to commit
  if git diff --cached --quiet; then
    echo -e "${RED}No changes for ${DEMO_NUM}, skipping${NC}"
    git checkout main
    git branch -D "${BRANCH}" || true
    continue
  fi

  # Commit using guideline format: <type>: <subject>\n<body>
  COMMIT_MSG="${DEMO_MESSAGES[$i]}
${DEMO_BODIES[$i]}"
  git commit -m "${COMMIT_MSG}"

  # Push branch
  echo "Pushing ${BRANCH}..."
  git push -u origin "${BRANCH}"

  # Create PR
  echo "Creating PR for ${BRANCH}..."
  PR_URL=$(gh pr create \
    --base main \
    --head "${BRANCH}" \
    --title "${DEMO_MESSAGES[$i]}" \
    --body "${DEMO_BODIES[$i]}

参考 demo001-015.md / demo015-020.md 的演进说明，此 PR 引入第 ${i} 个教学版本。")

  echo -e "${GREEN}PR created: ${PR_URL}${NC}"

  # Extract PR number from URL (last part after /)
  PR_NUM=$(echo "$PR_URL" | sed 's#.*/##')

  # Merge the PR immediately (to build the "past 20 PRs" history on main)
  echo "Merging PR #${PR_NUM} ..."
  gh pr merge "${PR_NUM}" --merge --delete-branch || gh pr merge "${PR_NUM}" --squash --delete-branch || true

  # Update local main
  git checkout main
  git pull --ff-only origin main

  echo -e "${GREEN}✓ demo${DEMO_NUM} PR 已合并到 main${NC}"
done

echo -e "\n${GREEN}=== 全部 20 个 PR 创建并合并完成！ ===${NC}"
echo "当前 main 分支提交历史："
git log --oneline -25 --graph --decorate

echo -e "\n远程 PR 列表："
gh pr list --state all --limit 25
