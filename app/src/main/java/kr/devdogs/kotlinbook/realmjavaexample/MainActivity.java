package kr.devdogs.kotlinbook.realmjavaexample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import kr.devdogs.kotlinbook.realmjavaexample.model.Student;

public class MainActivity extends AppCompatActivity {
    private Realm realm;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.view_txt) ;

        Realm.init(getApplicationContext());
        realm = Realm.getDefaultInstance();

        Student student1 = new Student();
        student1.setStudentId(1);
        student1.setName("박중수");
        student1.setAge(26);
        student1.setGrade(4);

        Student student2 = new Student();
        student2.setStudentId(2);
        student2.setName("박영환");
        student2.setAge(27);
        student2.setGrade(4);

        insertOrUpdateV1(student1);
        insertOrUpdateV2(student2);

        Student tempStudent = new Student();
        tempStudent.setStudentId(1);

        deleteById(1);

        List<Student> studentList = findAll();
        Student oneStudent = findOneById(1);


        StringBuilder sb = new StringBuilder();
        sb.append("== List ==\n");
        if(studentList != null) {
            for (Student student : studentList) {
                sb.append(student.getStudentId())
                        .append(". ")
                        .append(student.getName())
                        .append(" - ")
                        .append(student.getAge())
                        .append("살 - ")
                        .append(student.getGrade())
                        .append("학년\n");
            }
        }

        if(oneStudent != null) {
            sb.append("\n\n== Select One ==\n")
                    .append(oneStudent.getStudentId())
                    .append(". ")
                    .append(oneStudent.getName())
                    .append(" - ")
                    .append(oneStudent.getAge())
                    .append("살 - ")
                    .append(oneStudent.getGrade())
                    .append("학년\n");
        }

        textView.setText(sb.toString());
    }

    private void insertOrUpdateV1(final Student student) {
        realm.beginTransaction();
        if(student.getStudentId() == 0) {
            Number maxId = realm.where(Student.class).max("studentId");
            int nextId = maxId == null ? 1 : maxId.intValue() + 1;
            student.setStudentId(nextId);
        }

        realm.insertOrUpdate(student);
        realm.commitTransaction();
    }

    private void insertOrUpdateV2(final Student student) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                if(student.getStudentId() == 0) {
                    Number maxId = realm.where(Student.class).max("studentId");
                    int nextId = maxId == null ? 1 : maxId.intValue() + 1;
                    student.setStudentId(nextId);
                }

                realm.insertOrUpdate(student);
            }
        });
    }


    private List<Student> findAll() {
        RealmResults<Student> results = realm.where(Student.class)
                .findAll()
                .sort("studentId", Sort.DESCENDING);


        // 결과에서 특정 인덱스의 값도 불러올 수 있다
        // Student student = results.get(2);

        List<Student> list = results;

        return list;
    }

    private Student findOneById(int studentId) {
        Student results = realm.where(Student.class)
                .equalTo("studentId", studentId)
                .findFirst();

        return results;
    }


    private void deleteById(final int studentId) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Student targetStudent = realm.where(Student.class)
                        .equalTo("studentId", studentId)
                        .findFirst();

                targetStudent.deleteFromRealm();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        realm.close();
    }
}
