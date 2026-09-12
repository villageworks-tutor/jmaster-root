<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" buffer="none"%>
<%-- <%@ include file="/WEB-INF/views/taglib.jsp" %> --%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="ja">
	<head>
		<meta charset="UTF-8">
		<title>従業員検索 - 従業員データベース</title>
		<link rel="stylesheet" href="<c:url value="/css/style.css" />" />
	</head>
	<body>
		<header>
			<h1><a href="<c:url value="/" />">従業員データベース</a></h1>
		</header>
		<main>
			<article>
				<h2>従業員表示</h2>
				<section class="contents">
					<section class="criteria">
					<form action="<c:url value="/AppServlet" />" method="get">
						<table border="0">
							<tr>
								<th>
									<label for="name">氏名</label>
								</th>
								<td>
									<input id="name" type="text" name="name" value="${name}" placeholder="氏名" />
									<input type="hidden" name="action" value="search" />
									<button>検索</button>
								</td>
							</tr>
						</table>
					</form>
					</section>
					<section class="result">
					<c:choose>
						<c:when test="${empty requestScope.employees}">
						<p>従業員は見つかりませんでした。</p>
						</c:when>
						<c:otherwise>
						<p>${fn:length(requestScope.employees)}件の従業員が見つかりました。</p>
						<table border="1">
							<tr>
								<th>従業員番号</th>
								<th>部署番号</th>
								<th>従業員氏名</th>
								<th>内線番号</th>
								<th>入社日</th>
							</tr>
							<c:forEach items="${requestScope.employees}" var="employee">
							<tr>
								<td>${employee.id}</td>
								<td>${employee.departmentId}</td>
								<td>${employee.name}</td>
								<td>${employee.phone}</td>
								<td>${employee.hiredAt}</td>
							</tr>
							</c:forEach>
						</table>
						</c:otherwise>
					</c:choose>
					</section>
				</section>
			</article>
		</main>
		<footer>
			<div>&copy;2026 villagestyle.com</div>
		</footer>
	</body>
</html>