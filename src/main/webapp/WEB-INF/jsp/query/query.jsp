<%@ include file="../common/taglib.jsp"%>
<%
    String path = request.getContextPath();
    String _basePath = request.getScheme() + "://" + request.getServerName() + ":"
                       + request.getServerPort() + path + "/";
%>
<c:set value="<%=_basePath%>" var="basePath" />
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<base href="${basePath}">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>Query</title>
<%@ include file="../common/sheet_js.jsp"%>
</head>
<body>
	<div class="container">
		<%@ include file="../common/navi.jsp"%>
		<div class="row mtd5p">
			<div class="span12 text-justify pd80">This querying module
				allows flexible extraction of variation information from the
				database. Three steps guide users to query the database. 'Choose
				database' step gives a chance to determine the data source. 'Choose
				query type' step offers various ways that permit users flexibly
				extract what they need. And after query type is given, prompt
				message on input format will be shown in 'Input query words' part.</div>
		</div>

		<div class="row mtd5p">
			<div class="span12 text-justify pd80">
				<strong>Note: Release 5 coordinates are used in this
					database.</strong>
			</div>
		</div>

		<div class="row mtd10p pd80">
			<form:form id="queryForm" role="form" commandName="queryForm"
				method="post" action="${basePath}query/query.htm"
				enctype="multipart/form-data">
				<div class="row">
					<div
						class="col-xs-12 col-sm-4 col-md-4 col-lg-4 text-center pull-left vertical-center">
						<span class="inline-block vc115">choose database</span>
					</div>
					<div class="col-xs-12 col-sm-8 col-md-8 col-lg-8 pull-right">
						<label class="radio"> <form:radiobutton id="db_dgrp"
								path="variationDb" value="1" checked="checked" /> DGRP
						</label> <label class="radio"> <form:radiobutton id="db_hugo"
								path="variationDb" value="2" />EMS screening
						</label> <label class="radio"> <form:radiobutton id="db_other"
								path="variationDb" value="3" />Other public databases
						</label> <label class="radio"> <form:radiobutton
								id="db_dgrp_hugo_other" path="variationDb" value="4" />DGRP +
							EMS screening + Other public databases
						</label>
						<form:errors path="variationDb" cssClass="error" />
					</div>
				</div>

				<div class="row">
					<div
						class="col-xs-12 col-sm-4 col-md-4 col-lg-4 text-center pull-left">
						<span class="inline-block vc140">choose type</span>
					</div>
					<div class="col-xs-12 col-sm-8 col-md-8 col-lg-8 pull-right">
						<label class="radio"> <form:radiobutton path="queryType"
								value="1" checked="checked" onclick="queryByVariation()" /> by
							variation (format: chr pos ref alt)
						</label> <label class="radio"> <form:radiobutton path="queryType"
								value="2" onclick="queryBySample()" /> by sample
						</label> <label class="radio"> <form:radiobutton path="queryType"
								value="3" onclick="queryByRegion()" /> by genomic region
							(format: chr start end)
						</label> <label class="radio"> <form:radiobutton path="queryType"
								value="4" onclick="queryByGeneName()" /> by gene name(whole
							region) (format: geneName)
						</label> <label class="radio"> <form:radiobutton path="queryType"
								value="5" onclick="queryByGeneName()" /> by gene name(exon)
							(format: geneName)
						</label>
						<form:errors path="queryType" cssClass="error" />
					</div>
				</div>

				<div class="row mtd5p">
					<div
						class="col-xs-12 col-sm-4 col-md-4 col-lg-4 text-center pull-left">
						<span class="inline-block vc168">input or choose a file</span>
					</div>
					<div class="col-xs-12 col-sm-8 col-md-8 col-lg-8 pull-right">
						<div id="div_query_input" class="row">
							<form:textarea path="queryInput" rows="5" id="query_input"
								name="query_input" class="form-control" type="text"
								scrolling="auto" title="Example: chr2L 22025 T G"
								placeholder="The format of input: chr pos ref var. You can input multiple variations with one variation in a single line." />
						</div>
						<div id="div_select_sample" class="row dn">
							<form:select path="selectSample" id="select_sample"
								class="form-control">
								<option value="" selected="selected">--------------Select
									please-----------</option>
							</form:select>
							<form:errors path="selectSample" cssClass="error" />
						</div>
						<div id="div_input_email" class="row dn">
							<div class=" text-justify">Input e-mail address for
								getting your results:</div>
							<div class="">
								<form:input path="queryEmail" id="query_email"
									name="query_email" class="form-control"
									placeholder="input email address" />
								<form:errors path="queryEmail" cssClass="error" />
							</div>
						</div>
						<div id="div_choose_a_file" class="row">
							<div class="">Or Choose from a file:</div>
							<div class="">
								<input id="query_file" name="queryFile" type="file" class="file"
									data-show-preview="false">
							</div>
						</div>
						<span id="queryInputErrorPrompt" class="error dn"></span>
						<form:errors path="queryInput" cssClass="error" />
					</div>
				</div>


				<div class="row">
					<div
						class="col-xs-12 col-sm-4 col-md-4 col-lg-4 text-center pull-left">
						<span class="inline-block vc45">submit</span>
					</div>
					<div
						class="col-xs-12 col-sm-8 col-md-8 col-lg-8 text-center pull-right">
						<div
							class="col-xs-12 col-sm-6 col-md-6 col-lg-6 text-center pull-left">
							<button id="query" type="button" name="query" value="query"
								class="btn btn-default btn-lg btn-block">query</button>
						</div>
						<div
							class="col-xs-12 col-sm-6 col-md-6 col-lg-6 text-center pull-left">
							<button id="query_and_annotate" type="button"
								name="query_and_annotate"
								class="btn btn-default btn-lg btn-block"
								value="query_and_annotate">query and annotate</button>
						</div>
					</div>
				</div>
			</form:form>

		</div>
		<%@ include file="../common/footer.jsp"%>
		<script src="static/js/query.js"></script>
	</div>
</body>
</html>