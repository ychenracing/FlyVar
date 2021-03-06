/**
 * ychen. Copyright (c) 2016年10月30日.
 */
package cn.edu.fudan.iipl.flyvar.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

/**
 * 
 * @author racing
 * @version $Id: QueryResultVariation.java, v 0.1 2016年10月30日 下午3:49:06 racing Exp $
 */
public class QueryResultVariation implements Serializable {

    private static final long serialVersionUID = -8775523339995696046L;

    private String            chr;
    private long              pos;
    private String            ref;
    private String            alt;
    private Integer           count;

    public QueryResultVariation() {
    }

    public QueryResultVariation(String chr, long pos, String ref, String alt) {
        this.chr = chr;
        this.pos = pos;
        this.ref = ref;
        this.alt = alt;
    }

    public QueryResultVariation(String chr, long pos, String ref, String alt, int count) {
        this(chr, pos, ref, alt);
        this.count = count;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public long getPos() {
        return pos;
    }

    public void setPos(long pos) {
        this.pos = pos;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public static String convertToVcfLine(QueryResultVariation resultVariation) {
        return StringUtils.join(Lists.<String> newArrayList(resultVariation.getChr(),
            String.valueOf(resultVariation.getPos()), ".", resultVariation.getRef(),
            resultVariation.getAlt(), ".", ".", ".", ".", "."), "\t");
    }

    public static List<String> convertToVcfLines(Collection<QueryResultVariation> resultVariations) {
        return resultVariations.stream().map(variation -> convertToVcfLine(variation))
            .collect(Collectors.toList());
    }
}
