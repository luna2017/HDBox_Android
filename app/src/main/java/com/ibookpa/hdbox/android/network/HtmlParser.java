package com.ibookpa.hdbox.android.network;

import android.text.TextUtils;
import android.util.Log;

import com.ibookpa.hdbox.android.model.CourseModel;
import com.ibookpa.hdbox.android.model.ExamModel;
import com.ibookpa.hdbox.android.model.ScoreModel;
import com.ibookpa.hdbox.android.model.UserInfoModel;
import com.ibookpa.hdbox.android.persistence.User;

import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;

/**
 * Created by tc on 7/14/16. HTML 解析类
 */
public class HtmlParser {
    private static final String TAG = "HtmlParser";

    /**
     * 解析登录返回结果
     */
    public static boolean parseLoginResult(ResponseBody body) {
        try {
            String html = body.string();
            return parseLoginResult(html);
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 解析真实姓名
     */
    public static String parseRealName(ResponseBody body) {
        try {
            return parseRealName(Jsoup.parse(body.byteStream(), "utf-8", ""));
        } catch (IOException e) {
            Log.e(TAG, "--tc-->ParseRealName error:" + e.toString());
            return null;
        }
    }

    /**
     * 解析用户信息(不包括真实姓名)
     */
    public static UserInfoModel parseUserInfo(ResponseBody body) {
        try {
            String data = body.string();
            JSONObject jo = new JSONObject(data);
            return parseUserInfo(jo, User.currentUser().getUid());
        } catch (Exception e) {
            Log.e(TAG, "ParseUserInfo error:" + e.toString());
            return null;
        }
    }

    /**
     * 解析课表
     */
    public static List<CourseModel> parseCourse(ResponseBody body) {
        try {
            return parseCourse(Jsoup.parse(body.byteStream(), "utf-8", ""), User.currentUser().getUid());
        } catch (IOException e) {
            return null;
        }
    }


    /**
     * 解析成绩表
     */
    public static List<ScoreModel> parseScore(ResponseBody body) {
        try {
            return parseScores(Jsoup.parse(body.byteStream(), "utf-8", ""), User.currentUser().getUid());
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * 解析考试安排表
     */
    public static List<ExamModel> parseExam(ResponseBody body) {
        //TODO 解析考试安排表
        return null;
    }


    private static boolean parseLoginResult(String html) {
        return html.contains("handleLoginSuccess");//听天由命:)
    }


    private static String parseRealName(Document doc) {

        Element e = doc.getElementsByAttributeValue("id", "welcomeMsg").first();
        if (e == null) {
            return null;
        }

        return e.text().split("，")[0];
    }

    /**
     * 解析用户信息,注意这里从培养方案得到的数据中没有姓名字段
     */
    private static UserInfoModel parseUserInfo(JSONObject jo, String uid) {
        UserInfoModel userInfo = new UserInfoModel();

        String grade = jo.optString("njmc");
        String college = jo.optString("dwmc");
        String specialty = jo.optString("zymc");

        //确保得到的数据不为空,如果其中有一个字段为空,就返回 null
        if (TextUtils.isEmpty(grade) || TextUtils.isEmpty(college) || TextUtils.isEmpty(specialty)) {
            return null;
        }

        userInfo.setUid(uid);
        userInfo.setGrade(grade);
        userInfo.setCollege(college);
        userInfo.setSpecialty(specialty);

        return userInfo;
    }


    /**
     * 解析课程表
     * 从课表的 HTML 代码中得到的 < table > 标签中,如果上一行中有一列要占据两行(rowspan>1),则下一行对应列没有 < td >标签
     * 举例:td 表示 一个 tr 标签中的 td 标签,括号中的数字表示 rowspan 的值
     * <p/>
     * td(1),td(2),td(1),td(1),td(2),td(1)
     * td(1),td(1),td(1),td(1)
     * <p/>
     * 可以看到,第二行只有四个 td 标签,那是因为第一行有两个 td 标签的 rowspan 值为 2,导致下一行对应列直接跳过
     * 问题:
     * 这样就没有办法拿到对应课程是在周几!!!
     * <p/>
     * 解决:按照 11 行 7 列循环读取,如果存在上一行,则拿到上一行对应列的数据,判断其 rowSpan 值是否大于 1,如果是,新建一个 node
     * 并且赋值为上个数据的 rowSpan-1,如果不是,则拿 HTML 中对应的数据赋值给新建的 node
     * 创建了一个 11 行 7 列的二维 list,再对指定数据解析
     */
    private static List<CourseModel> parseCourse(Document doc, String uid) {

        Element table = doc.getElementsByClass("CourseFormTable").first();
        if (table == null) {
            return null;
        }
        Elements trs = table.getElementsByTag("tr");

        List<List<CourseNode>> courseTable = new ArrayList<>();

        // i=[1,11]section
        for (int i = 1; i < 12; i++) {
            Elements tds = trs.get(i).getElementsByTag("td");

            List<CourseNode> lastCols = i <= 1 ? null : courseTable.get(i - 2);

            List<CourseNode> cols = new ArrayList<>();
            int tdIndex = 2;
            // j=[1,7]dayOfWeek
            for (int j = 1; j < 8; j++) {
                if (lastCols == null) {
                    Element td = tds.get(j + 1);
                    CourseNode node = new CourseNode(td.text(), Integer.valueOf(td.attr("rowspan")));
                    cols.add(node);
                } else {
                    CourseNode lastNode = lastCols.get(j - 1);
                    if (lastNode.rowSpan > 1) {
                        CourseNode newNode = new CourseNode("", lastNode.rowSpan - 1);
                        cols.add(newNode);
                    } else {
                        Element td = tds.get(tdIndex++);
                        CourseNode node = new CourseNode(td.text(), Integer.valueOf(td.attr("rowspan")));
                        cols.add(node);
                    }
                }
            }
            courseTable.add(cols);
        }
        //已经按照格式存放在 courseTable 中,接下来循环解析每节课
        List<CourseModel> results = new ArrayList<>();
        for (int i = 0; i < courseTable.size(); i++) {
            List<CourseNode> cols = courseTable.get(i);
            for (int j = 0; j < cols.size(); j++) {
                CourseNode node = cols.get(j);
                if (node.data.length() < 2) {
                    continue;
                }
                results.add(parseCourseItem(node.data, j + 1, uid));
            }
        }
        return results;
    }


    /**
     * 解析单个课程
     */
    private static CourseModel parseCourseItem(String data, int dayOfWeek, String uid) {
        String[] items = data.split(" ");
        CourseModel course = new CourseModel();
        if (items.length >= 6) {
            course.setUid(uid);
            course.setDayOfWeek(dayOfWeek);

            course.setCname(items[0] == null ? "" : items[0]);

            String week = items[1] == null ? "" : items[1];
            String section = items[2] == null ? "" : items[2];

            Pattern p = Pattern.compile("[\\u4e00-\\u9fa5\\-\\(\\)\\s*]+");

            String[] ws = p.split(week);
            String[] ss = p.split(section);

            // 解析开始周次和结束周次
            if (ws != null && ws.length >= 2) {
                try {
                    course.setStartWeek(Integer.valueOf(ws[1]));
                    course.setEndWeek(Integer.valueOf(ws[2]));
                } catch (Exception e) {
                    course.setStartWeek(0);
                    course.setEndWeek(0);
                }
            } else {
                course.setStartWeek(0);
                course.setEndWeek(0);
            }

            //解析开始节次和结束节次
            if (ss != null && ss.length >= 3) {
                try {
                    course.setStartSection(Integer.valueOf(ss[1]));
                    course.setEndSection(Integer.valueOf(ss[2]));
                } catch (Exception e) {
                    course.setStartSection(0);
                    course.setEndSection(0);
                }
            } else {
                course.setStartSection(0);
                course.setEndSection(0);
            }

            course.setTeacher(items[3] == null ? "" : items[3]);
            course.setClassroom(items[5] == null ? "" : items[5]);

            return course;
        } else {
            Log.i("TAG","--tc-->Parse error");
            return null;
        }
    }

    /**
     * 解析成绩表
     */
    private static List<ScoreModel> parseScores(Document doc, String uid) {
        List<ScoreModel> results = new ArrayList<>();

        // 找到有效成绩的 Div
        Element div = doc.getElementsByAttributeValue("tabid", "01").first();
        if (div == null) {
            return null;
        }
        Elements trs = div.select("tr.t_con");

        for (int i = 0; i < trs.size(); i++) {
            Elements tds = trs.get(i).getElementsByTag("td");

            //确保拿到正确的数据
            if (tds.size() < 8) {
                continue;
            }

            ScoreModel score = new ScoreModel();
            score.setUid(uid);

            String[] terms = tds.get(1).text().split(" ");//将 "2015-2016 第二学期"按空格分成前面的学年和后面的学期
            if (terms.length > 1) {
                score.setSchoolYear(terms[0]);
                score.setTerm(terms[1]);
            }

            score.setCid(tds.get(2).text().trim().equals("") ? "--" : tds.get(2).text().trim());
            score.setCname(tds.get(3).text().trim().equals("") ? "--" : tds.get(3).text().trim());
            score.setType(tds.get(4).text().trim().equals("") ? "--" : tds.get(4).text().trim());
            score.setProperty(tds.get(5).text().trim().equals("") ? "--" : tds.get(5).text().trim());

            try {
                // 如果有重修课程,一格里面会有两条数据,所以需要切分
                score.setCredit(Float.valueOf(tds.get(6).text().split(" ")[0]));
            } catch (Exception e) {
                score.setCredit(0.0f);
            }

            try {
                score.setScore(Float.valueOf(tds.get(7).text().trim()));
            } catch (Exception e) {
                score.setScore(0.0f);
            }

            score.setStudyMethod(tds.get(8).text().trim().length() == 1 ? "初修" : tds.get(8).text().trim());
            results.add(score);
        }
        return results;
    }


    /**
     * 暂时这样,以后再改,fuck
     */
    private static int getDayInt(String dayStr) {
        int day = 1;

        switch (dayStr) {
            case "星期一":
                day = 1;
                break;
            case "星期二":
                day = 2;
                break;
            case "星期三":
                day = 3;
                break;
            case "星期四":
                day = 4;
                break;
            case "星期五":
                day = 5;
                break;
            case "星期六":
                day = 6;
                break;
            case "星期日":
                day = 7;
                break;
            default:
                day = 1;
        }

        return day;
    }


    static class CourseNode {
        private String data;
        private int rowSpan;

        public CourseNode(String data, int rowSpan) {
            this.data = data;
            this.rowSpan = rowSpan;
        }
    }

}
