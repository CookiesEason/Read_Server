package com.xzy.read.util.algorithm;

import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Term;
import org.ansj.splitWord.Analysis;
import org.ansj.splitWord.analysis.NlpAnalysis;
import org.nlpcn.commons.lang.util.StringUtil;

import java.util.*;

/**
 * @author XieZhongYi
 * 2020/04/13 12:21
 */
public class KeyWordComputerSub<T extends Analysis> {

    private static final Map<String, Double> POS_SCORE = new HashMap();
    private T analysisType;
    private int nKeyword = 5;

    public KeyWordComputerSub() {
    }

    public void setAnalysisType(T analysisType) {
        this.analysisType = analysisType;
    }

    public KeyWordComputerSub(int nKeyword) {
        this.nKeyword = nKeyword;
        this.analysisType = (T) new NlpAnalysis();
    }

    public KeyWordComputerSub(int nKeyword, T analysisType) {
        this.nKeyword = nKeyword;
        this.analysisType = analysisType;
    }

    private List<Keyword> computeArticleTfidf(String content, int titleLength) {
        Map<String, Keyword> tm = new HashMap();
        List<Term> parse = this.analysisType.parseStr(content).getTerms();
        Iterator var5 = parse.iterator();

        while(var5.hasNext()) {
            Term term = (Term)var5.next();
            double weight = this.getWeight(term, content.length(), titleLength);
            if (weight != 0.0D) {
                Keyword keyword = (Keyword)tm.get(term.getName());
                if (keyword == null) {
                    keyword = new Keyword(term.getName(), term.termNatures().allFreq, weight);
                    tm.put(term.getName(), keyword);
                } else {
                    keyword.updateWeight(1);
                }
            }
        }

        TreeSet<Keyword> treeSet = new TreeSet(tm.values());
        ArrayList<Keyword> arrayList = new ArrayList(treeSet);
        if (treeSet.size() <= this.nKeyword) {
            return arrayList;
        } else {
            return arrayList.subList(0, this.nKeyword);
        }
    }

    public List<Keyword> computeArticleTfidf(String title, String content) {
        if (StringUtil.isBlank(title)) {
            title = "";
        }

        if (StringUtil.isBlank(content)) {
            content = "";
        }

        return this.computeArticleTfidf(title + "\t" + content, title.length());
    }

    public List<Keyword> computeArticleTfidf(String content) {
        return this.computeArticleTfidf(content, 0);
    }

    private double getWeight(Term term, int length, int titleLength) {
        if (term.getName().trim().length() < 2) {
            return 0.0D;
        } else {
            String pos = term.natrue().natureStr;
            Double posScore = (Double)POS_SCORE.get(pos);
            if (posScore == null) {
                posScore = 1.0D;
            } else if (posScore == 0.0D) {
                return 0.0D;
            }

            return titleLength > term.getOffe() ? 5.0D * posScore : (double)(length - term.getOffe()) * posScore / (double)length;
        }
    }

    static {
        POS_SCORE.put("null", 0.0D);
        POS_SCORE.put("w", 0.0D);
        POS_SCORE.put("en", 2.0D);
        POS_SCORE.put("m", 0.0D);
        POS_SCORE.put("num", 0.0D);
        POS_SCORE.put("nr", 3.0D);
        POS_SCORE.put("nrf", 3.0D);
        POS_SCORE.put("nw", 3.0D);
        POS_SCORE.put("nt", 3.0D);
        POS_SCORE.put("l", 0.2D);
        POS_SCORE.put("a", 0.2D);
        POS_SCORE.put("nz", 3.0D);
        POS_SCORE.put("v", 0.2D);
        POS_SCORE.put("kw", 6.0D);
    }

}
