package com.ibookpa.hdbox.android.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ibookpa.hdbox.android.R;
import com.ibookpa.hdbox.android.manager.DownloadService;
import com.ibookpa.hdbox.android.model.CheckUpdateModel;
import com.ibookpa.hdbox.android.model.CourseModel;
import com.ibookpa.hdbox.android.model.ScoreModel;
import com.ibookpa.hdbox.android.utils.DateUtil;

/**
 * Created by tc on 6/29/16. 课表点击单个详情弹出框
 */
public class CustomDialog {

    /**
     * 显示课表详情
     */
    public static void showCourseItemDialog(Context mContext, CourseModel course) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setTitle("课程详情");

        View mView = LayoutInflater.from(mContext).inflate(R.layout.view_course_item_detail, null);
        builder.setView(mView);
        attachData(mView, course);

        builder.setCancelable(true);
        builder.show();
    }

    private static void attachData(View view, CourseModel course) {
        TextView tvCourseName = (TextView) view.findViewById(R.id.tv_course_item_name);
        TextView tvCourseTeacher = (TextView) view.findViewById(R.id.tv_course_item_teacher);
        TextView tvCourseTime = (TextView) view.findViewById(R.id.tv_course_item_time);
        TextView tvCourseWeek = (TextView) view.findViewById(R.id.tv_course_item_week);
        TextView tvCourseLocation = (TextView) view.findViewById(R.id.tv_course_item_location);

        tvCourseName.setText(course.getCname());
        tvCourseTeacher.setText(course.getTeacher());

        tvCourseTime.setText(new StringBuilder(DateUtil.dayOfWeekStr(course.getDayOfWeek()) + " " + course.getStartSection() + "-" + course.getEndSection() + "节"));
        tvCourseWeek.setText(new StringBuilder(course.getStartWeek() + "-" + course.getEndWeek() + "周"));
        tvCourseLocation.setText(course.getClassroom());
    }

//----------------------------------------------------------------------------------------------------------

    /**
     * 显示成绩详情
     * <p/>
     * 由于系统 bug,AlertDialog 不能在 ScoreShowRecyclerAdapter 中设置,会报 NullPointerException
     * Attempt to invoke virtual method `android.content.res.Resources$Theme android.content.Context.getTheme()` on a null object reference
     * 原因大概是 Context 对象的问题,因此将其放在 fragment 中通过 getActivity()作为 Context 就正常了
     */
    public static void showScoreItemDialog(Context context, ScoreModel score) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        builder.setTitle("成绩详情");

        View mView = LayoutInflater.from(context).inflate(R.layout.view_score_item_detail, null);
        builder.setView(mView);
        bindDialogView(mView, score);
        builder.setCancelable(true);
        builder.show();
    }

    private static void bindDialogView(View view, ScoreModel score) {

        TextView tvTerm = (TextView) view.findViewById(R.id.tv_score_item_detail_term);
        TextView tvCourseId = (TextView) view.findViewById(R.id.tv_score_item_detail_course_id);
        TextView tvCourseName = (TextView) view.findViewById(R.id.tv_score_item_detail_course_name);
        TextView tvCourseType = (TextView) view.findViewById(R.id.tv_score_item_detail_course_type);
        TextView tvCourseProperty = (TextView) view.findViewById(R.id.tv_score_item_detail_course_property);
        TextView tvStudyMethod = (TextView) view.findViewById(R.id.tv_score_item_detail_study_method);
        TextView tvCredit = (TextView) view.findViewById(R.id.tv_score_item_detail_credit);
        TextView tvScore = (TextView) view.findViewById(R.id.tv_score_item_detail_score);

        tvTerm.setText(new StringBuilder(score.getSchoolYear() + " " + score.getTerm()));
        tvCourseId.setText(score.getCid());
        tvCourseName.setText(score.getCname());
        tvCourseType.setText(score.getType());
        tvCourseProperty.setText(score.getProperty());
        tvStudyMethod.setText(score.getStudyMethod());
        tvCredit.setText(String.valueOf(score.getCredit()));
        tvScore.setText(String.valueOf(score.getScore()));
    }
//---------------------------------------------------------------------------------------------------------

    /**
     * 显示新版本提示
     */
    public static void showNewVersionDialog(final Context context, final CheckUpdateModel model) {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);

        builder.setTitle("发现新版本 " + model.getVersionName());
        builder.setMessage(model.getDescription());

        builder.setPositiveButton("立即更新", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toDownload = new Intent(context, DownloadService.class);
                toDownload.putExtra("versionName", model.getVersionName());
                toDownload.putExtra("url", model.getUrl());
                context.startService(toDownload);
            }
        });
        builder.setNegativeButton("稍后再说", null);
        builder.setCancelable(true);
        builder.show();
    }

}
