package com.sevenlearn.a7learnstudents;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.textfield.TextInputEditText;

import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AddNewStudentFormActivity extends AppCompatActivity {
    private static final String TAG = "AddNewStudentFormActivi";
    private ApiService apiService;
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_student_form);
        apiService=new ApiService(this,TAG);

        Toolbar toolbar = findViewById(R.id.toolbar_addNewStudent);
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_back_white_24dp);

        final TextInputEditText firstNameEt = findViewById(R.id.et_addNewStudent_firstName);
        final TextInputEditText lastNameEt = findViewById(R.id.et_addNewStudent_lastName);
        final TextInputEditText courseEt = findViewById(R.id.et_addNewStudent_course);
        final TextInputEditText scoreEt = findViewById(R.id.et_addNewStudent_score);

        View saveBtn = findViewById(R.id.fab_addNewStudent_save);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (firstNameEt.length() > 0 &&
                        lastNameEt.length() > 0 &&
                        courseEt.length() > 0 &&
                        scoreEt.length() > 0) {
                    apiService.saveStudent(firstNameEt.getText().toString(),
                            lastNameEt.getText().toString(),
                            courseEt.getText().toString(),
                            Integer.parseInt(scoreEt.getText().toString()))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<Student>() {
                        @Override
                        public void onSubscribe(@io.reactivex.annotations.NonNull Disposable d) {
                            disposable = d;
                        }

                        @Override
                        public void onSuccess(@io.reactivex.annotations.NonNull Student student) {
                            Intent intent=new Intent();
                            intent.putExtra("student",student);
                            setResult(Activity.RESULT_OK,intent);
                            finish();
                        }

                        @Override
                        public void onError(@io.reactivex.annotations.NonNull Throwable e) {
                            Toast.makeText(AddNewStudentFormActivity.this, "خطای نامشخص", Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (disposable != null){
            disposable.dispose();
        }
        apiService.cancel();
    }
}
