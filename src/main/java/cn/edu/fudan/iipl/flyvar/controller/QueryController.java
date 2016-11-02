/**
 * ychen. Copyright (c) 2016年10月26日.
 */
package cn.edu.fudan.iipl.flyvar.controller;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import cn.edu.fudan.iipl.flyvar.AbstractController;
import cn.edu.fudan.iipl.flyvar.form.QueryForm;
import cn.edu.fudan.iipl.flyvar.model.QueryResultVariation;
import cn.edu.fudan.iipl.flyvar.model.Variation;
import cn.edu.fudan.iipl.flyvar.model.VariationRegion;
import cn.edu.fudan.iipl.flyvar.model.constants.QueryType;
import cn.edu.fudan.iipl.flyvar.model.constants.VariationDataBaseType;
import cn.edu.fudan.iipl.flyvar.service.QueryService;
import cn.edu.fudan.iipl.flyvar.service.SampleNameService;

/**
 * variantion query控制器
 * 
 * @author racing
 * @version $Id: QueryController.java, v 0.1 2016年10月26日 下午10:02:56 racing Exp $
 */
@Controller
public class QueryController extends AbstractController {

    private static final Logger logger             = LoggerFactory.getLogger(QueryController.class);

    private static final String QUERY_JSP          = "query/query";

    private static final String QUERY_RESULT_JSP   = "query/queryResult";

    private static final String UPLOADED_FILE_PATH = "/WEB-INF/file";

    @Autowired
    private SampleNameService   sampleNameService;

    @Autowired
    private QueryService        queryService;

    @RequestMapping(value = { "/query.htm" }, method = { RequestMethod.GET })
    public String showQuery(Model model) {
        model.addAttribute("queryForm", new QueryForm());
        return QUERY_JSP;
    }

    @RequestMapping(value = { "/query/getDgrpSampleList.json" }, method = { RequestMethod.GET,
                                                                            RequestMethod.POST })
    @ResponseBody
    public Map<String, List<String>> getSampleNames(HttpServletRequest request) {
        checkReferer(request);
        Map<String, List<String>> result = Maps.newHashMap();
        result.put("data", sampleNameService.getSampleNames());
        return result;
    }

    @RequestMapping(value = { "/query/query.htm" }, method = { RequestMethod.POST })
    public String doQuery(HttpServletRequest request, @Valid QueryForm queryForm,
                          BindingResult bindings, MultipartFile queryFile,
                          RedirectAttributes redirectModel, Model model) {
        checkReferer(request);
        boolean correctParams = validateQueryParams(request, queryForm, bindings, queryFile, model);
        if (!correctParams) {
            return QUERY_JSP;
        }
        QueryType queryType = QueryType.of(queryForm.getQueryType());
        if (QueryType.VARIATION == queryType) {
            Set<Variation> variations = null;
            String variationStr = queryForm.getQueryInput();
            if (StringUtils.isBlank(queryForm.getQueryInput())) {
                try {
                    variationStr = FileUtils
                        .readFileToString(saveFileAndGetFilePath(queryFile).toFile(), "utf-8");
                } catch (IOException e) {
                    logger.error("read file error! queryFile=" + queryFile, e);
                }
            }
            variations = Variation.convertInputToVariations(variationStr);
            if (variations == null) {
                model.addAttribute("queryForm", queryForm);
                bindings.rejectValue("queryInput", "error.queryInputFormat");
                logger.info("error submit! error format for variation input or file: queryForm={}",
                    queryForm);
                return QUERY_JSP;
            }
            List<QueryResultVariation> queryResult = queryService.queryByVariation(variations,
                VariationDataBaseType.of(queryForm.getVariationDb()));
            redirectModel.addFlashAttribute("queryResults", queryResult);
            redirectModel.addFlashAttribute("queryType", queryForm.getVariationDb());
            return "redirect:/queryResult.htm";
        } else if (QueryType.SAMPLE == queryType) {

        } else if (QueryType.REGION == queryType) {
            Set<VariationRegion> regions = null;
            String regionStr = queryForm.getQueryInput();
            if (StringUtils.isBlank(queryForm.getQueryInput())) {
                try {
                    regionStr = FileUtils
                        .readFileToString(saveFileAndGetFilePath(queryFile).toFile(), "utf-8");
                } catch (IOException e) {
                    logger.error("read file error! queryFile=" + queryFile, e);
                }
            }
            regions = VariationRegion.convertInputToRegions(regionStr);
            if (regions == null) {
                model.addAttribute("queryForm", queryForm);
                bindings.rejectValue("queryInput", "error.queryInputFormat");
                logger.info("error submit! error format for variation input or file: queryForm={}",
                    queryForm);
                return QUERY_JSP;
            }
            List<QueryResultVariation> queryResult = queryService.queryByRegion(regions,
                VariationDataBaseType.of(queryForm.getVariationDb()));
            redirectModel.addFlashAttribute("queryResults", queryResult);
            redirectModel.addFlashAttribute("queryType", queryForm.getVariationDb());
            return "redirect:/queryResult.htm";
        } else if (QueryType.GENE_WHOLE == queryType) {
            String variationStr = queryForm.getQueryInput();
            if (StringUtils.isBlank(queryForm.getQueryInput())) {
                try {
                    variationStr = FileUtils
                        .readFileToString(saveFileAndGetFilePath(queryFile).toFile(), "utf-8");
                } catch (IOException e) {
                    logger.error("read file error! queryFile=" + queryFile, e);
                }
            }
            if (StringUtils.isBlank(variationStr)) {
                model.addAttribute("queryForm", queryForm);
                bindings.rejectValue("queryInput", "error.queryInputFormat");
                logger.info("error submit! error format for variation input or file: queryForm={}",
                    queryForm);
                return QUERY_JSP;
            }
            List<String> geneNames = Lists.newArrayList(variationStr.split("\\s+"));
            List<QueryResultVariation> queryResult = queryService.queryByGeneNameWholeRegion(
                geneNames, VariationDataBaseType.of(queryForm.getVariationDb()));
            redirectModel.addFlashAttribute("queryResults", queryResult);
            redirectModel.addFlashAttribute("queryType", queryForm.getVariationDb());
            return "redirect:/queryResult.htm";
        } else if (QueryType.GENE_EXON == queryType) {
            String variationStr = queryForm.getQueryInput();
            if (StringUtils.isBlank(queryForm.getQueryInput())) {
                try {
                    variationStr = FileUtils
                        .readFileToString(saveFileAndGetFilePath(queryFile).toFile(), "utf-8");
                } catch (IOException e) {
                    logger.error("read file error! queryFile=" + queryFile, e);
                }
            }
            if (StringUtils.isBlank(variationStr)) {
                model.addAttribute("queryForm", queryForm);
                bindings.rejectValue("queryInput", "error.queryInputFormat");
                logger.info("error submit! error format for variation input or file: queryForm={}",
                    queryForm);
                return QUERY_JSP;
            }
            List<String> geneNames = Lists.newArrayList(variationStr.split("\\s+"));
            List<QueryResultVariation> queryResult = queryService.queryByGeneNameExonRegion(
                geneNames, VariationDataBaseType.of(queryForm.getVariationDb()));
            redirectModel.addFlashAttribute("queryResults", queryResult);
            redirectModel.addFlashAttribute("queryType", queryForm.getVariationDb());
            return "redirect:/queryResult.htm";
        }
        return null;
    }

    @RequestMapping(value = { "/query/queryAnnotate.htm" }, method = { RequestMethod.POST })
    public String doQueryAndAnnotate(HttpServletRequest request, @Valid QueryForm queryForm,
                                     BindingResult bindings, MultipartFile queryFile, Model model) {
        checkReferer(request);
        boolean collectedParams = validateQueryParams(request, queryForm, bindings, queryFile,
            model);
        if (!collectedParams) {
            return QUERY_JSP;
        }

        Set<Variation> variations = null;
        String variationStr = queryForm.getQueryInput();
        if (StringUtils.isBlank(queryForm.getQueryInput())) {
            try {
                variationStr = FileUtils
                    .readFileToString(saveFileAndGetFilePath(queryFile).toFile(), "utf-8");
            } catch (IOException e) {
                logger.error("read file error! queryFile=" + queryFile, e);
            }
        }
        variations = Variation.convertInputToVariations(variationStr);
        if (variations == null) {
            model.addAttribute("queryForm", queryForm);
            bindings.rejectValue("queryInput", "error.queryInputFormat");
            return QUERY_JSP;
        }

        return null;
    }

    @RequestMapping(value = { "/queryResult.htm" }, method = { RequestMethod.GET,
                                                               RequestMethod.POST })
    public String showQueryResults(HttpServletRequest request, Model model) {
        checkReferer(request);
        if (!model.containsAttribute("queryResults")) {
            throw new RuntimeException("Invalid access!");
        }
        return QUERY_RESULT_JSP;
    }

    private Path saveFileAndGetFilePath(MultipartFile file) {
        String originName = file.getOriginalFilename();
        String baseName = FilenameUtils.getBaseName(originName);
        String extName = FilenameUtils.getExtension(originName);
        StringBuilder newFilePath = new StringBuilder(UPLOADED_FILE_PATH).append(baseName)
            .append("_").append(System.currentTimeMillis()).append(RandomUtils.nextInt(0, 1000))
            .append(".").append(extName);
        try {
            file.transferTo(FileUtils.getFile(newFilePath.toString()));
        } catch (IllegalStateException | IOException e) {
            logger.error("save uploaded file error! newFilePath=" + newFilePath, e);
        }
        return Paths.get(newFilePath.toString());
    }

    private boolean validateQueryParams(HttpServletRequest request, @Valid QueryForm queryForm,
                                        BindingResult bindings, MultipartFile queryFile,
                                        Model model) {
        if (bindings.hasErrors()) {
            model.addAttribute("queryForm", queryForm);
            logger.info("error submit! QueryForm is empty: queryForm={}", queryForm);
            return false;
        }

        if (QueryType.SAMPLE == QueryType.of(queryForm.getQueryType())) {
            if (StringUtils.isBlank(queryForm.getSelectSample())) {
                model.addAttribute("queryForm", queryForm);
                bindings.rejectValue("selectSample", "error.selectSample");
                logger.info("error submit! selectSample is empty: queryForm={}", queryForm);
                return false;
            }
            if (!queryForm.getQueryEmail().matches("^(.+)@(.+)$")) {
                model.addAttribute("queryForm", queryForm);
                bindings.rejectValue("queryEmail", "error.queryEmail");
                logger.info("error submit! queryEmail is empty: queryForm={}", queryForm);
                return false;
            }
            return true;
        }

        if (StringUtils.isBlank(queryForm.getQueryInput()) && queryFile.isEmpty()) {
            logger.info(
                "error submit! Both queryForm and queryFile are empty!" + getClientIP(request));
            model.addAttribute("queryForm", queryForm);
            bindings.rejectValue("queryInput", "error.queryInput");
            return false;
        }

        return true;
    }

}
