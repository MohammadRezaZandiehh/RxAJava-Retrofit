package com.sevenlearn.a7learnstudents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Observer;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final Integer ADD_STUDENT_REQUEST_ID = 1001;
    private StudentAdapter studentAdapter;
    private RecyclerView recyclerView;
    private ApiService apiService;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        apiService = new ApiService(this, TAG);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        View addNewStudentBtn = findViewById(R.id.fab_main_addNewStudent);
        addNewStudentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MainActivity.this, AddNewStudentFormActivity.class), ADD_STUDENT_REQUEST_ID);
            }
        });


        apiService.getStudents()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<List<Student>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                        disposable = d;
                    }

                    @Override
                    public void onSuccess(@NonNull List<Student> students) {
                        recyclerView = findViewById(R.id.rv_main_students);
                        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this, RecyclerView.VERTICAL, false));
                        studentAdapter = new StudentAdapter(students);
                        recyclerView.setAdapter(studentAdapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Toast.makeText(MainActivity.this, "خطای نامشخص", Toast.LENGTH_SHORT).show();

                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ADD_STUDENT_REQUEST_ID && resultCode == Activity.RESULT_OK) {
            if (data != null && studentAdapter != null && recyclerView != null) {
                Student student = data.getParcelableExtra("student");
                studentAdapter.addStudent(student);
                recyclerView.smoothScrollToPosition(0);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null) {
            disposable.dispose();
        }
        apiService.cancel();
    }
}
