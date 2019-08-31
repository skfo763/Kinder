package com.KidsCampus.user.kinder;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.esafirm.imagepicker.features.ImagePicker;
import com.KidsCampus.user.kinder.Login.FindActivity;
import com.KidsCampus.user.kinder.Login.LoginActivity;
import com.KidsCampus.user.kinder.Notice.NoticeActivity;
import com.KidsCampus.user.kinder.Notice.ShowCommunityRuleActivity;
import com.KidsCampus.user.kinder.Notice.ShowInfoRuleActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.UploadTask;

import java.util.Map;
import java.util.Objects;

public class PrivateInfoActivity extends AppCompatActivity {

    private ImageView goback, profile;
    private TextView nickname, logout, changepw, changenickname, changeprofile;
    private TextView appinfo, notice, communityrule, inforule, license;
    private LinearLayout waiting;
    private String dwnUri = "https://firebasestorage.googleapis.com/v0/b/kinder-114b0.appspot.com/o/%EC%82%AC%EC%9A%A9%EC%9E%90%2F%ED%94%84%EB%A1%9C%ED%95%84%2F%EA%B8%B0%EB%B3%B8%20%ED%94%84%EB%A1%9C%ED%95%84%20%EC%9D%B4%EB%AF%B8%EC%A7%80%2Fimage?alt=media&token=af2e1806-9e11-443e-a40f-8f5bc8fb7c52";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private_info);

        goback = findViewById(R.id.privateinfo_goback);
        profile = findViewById(R.id.privateinfo_profile);
        nickname = findViewById(R.id.privateinfo_nickname);
        logout = findViewById(R.id.privateinfo_logout);
        changepw = findViewById(R.id.privateinfo_changepw);
        changenickname = findViewById(R.id.privateinfo_changenickname);
        changeprofile = findViewById(R.id.privateinfo_changeprofile);
        waiting = findViewById(R.id.privateinfo_waiting);
        appinfo = findViewById(R.id.privateinfo_appinfo);
        notice = findViewById(R.id.privateinfo_notice);
        communityrule = findViewById(R.id.privateinfo_coumnityrule);
        inforule = findViewById(R.id.privateinfo_inforule);
        license = findViewById(R.id.privateinfo_opensource_license);
        
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            Glide.with(this).load(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getPhotoUrl())
                    .into(profile);
            nickname.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getDisplayName());
            logout.setText("로그아웃");
        } else {
            nickname.setText("(미로그인)");
            logout.setText("로그인 화면으로");
        }
        
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(PrivateInfoActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(PrivateInfoActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                
            }
        });

        changepw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FirebaseFirestore.getInstance().collection("사용자").document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            if(documentSnapshot.exists()) {
                                String eAddress = documentSnapshot.get("id").toString();
                                FirebaseAuth.getInstance().sendPasswordResetEmail(eAddress).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        final AlertDialog.Builder builder = new AlertDialog.Builder(PrivateInfoActivity.this);
                                        builder.setTitle("비밀번호 재설정 안내")
                                                .setMessage("비밀번호 재설정 메일을 귀하의 이메일 주소로 전송하였습니다. 확인 및 비밀번호 변경 후 재로그인하시기 바랍니다.")
                                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        onBackPressed();
                                                    }
                                                })
                                                .show();

                                        new Handler().postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                onBackPressed();
                                            }
                                        }, 3000);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PrivateInfoActivity.this, "존재하지 않는 이메일입니다.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(PrivateInfoActivity.this, "계정 정보를 불러오는데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PrivateInfoActivity.this);
                    dialog.setTitle("로그인 알림")
                            .setMessage("로그인 후 사용가능한 기능입니다.\n로그인 하시겠습니까?")
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(PrivateInfoActivity.this, "취소하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            }).setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PrivateInfoActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
            }
        });

        changenickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    FrameLayout container = new FrameLayout(PrivateInfoActivity.this);
                    final EditText et = new EditText(PrivateInfoActivity.this);
                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    params.leftMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                    params.rightMargin = getResources().getDimensionPixelSize(R.dimen.dialog_margin);
                    et.setLayoutParams(params);
                    container.addView(et);

                    et.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    et.setPadding(15, 10, 15, 10);
                    AlertDialog.Builder builder = new AlertDialog.Builder(PrivateInfoActivity.this);
                    builder.setTitle("닉네임 변경하기")
                            .setView(container)
                            .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(PrivateInfoActivity.this, "취소하셨습니다", Toast.LENGTH_SHORT).show();
                                }
                            }).setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            UserProfileChangeRequest reques2t = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(et.getText().toString())
                                    .build();

                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(reques2t).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    nickname.setText(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getDisplayName()));
                                    Toast.makeText(PrivateInfoActivity.this, "닉네임을 변경했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PrivateInfoActivity.this);
                    dialog.setTitle("로그인 알림")
                            .setMessage("로그인 후 사용가능한 기능입니다.\n로그인 하시겠습니까?")
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(PrivateInfoActivity.this, "취소하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            }).setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PrivateInfoActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
                
            }
        });

        changeprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    String selc[] = {"프로필 이미지 변경", "프로필 이미지 삭제"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(PrivateInfoActivity.this);
                    builder.setItems(selc, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(which == 0) {
                                ImagePicker.create(PrivateInfoActivity.this)
                                        .toolbarFolderTitle("Folder") // folder selection title
                                        .toolbarImageTitle("Tap to select") // image selection title
                                        .toolbarArrowColor(Color.BLACK) // Toolbar 'up' arrow color
                                        .single()
                                        .includeVideo(false)
                                        .enableLog(false)
                                        .start();
                            } else if(which == 1){
                                waiting.setVisibility(View.VISIBLE);
                                UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                        .setPhotoUri(Uri.parse(dwnUri))
                                        .build();

                                FirebaseStorage.getInstance().getReferenceFromUrl("gs://kinder-114b0.appspot.com/사용자/프로필")
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete();

                                FirebaseAuth.getInstance().getCurrentUser().updateProfile(request)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Glide.with(PrivateInfoActivity.this).load(dwnUri).into(profile);
                                                waiting.setVisibility(View.GONE);
                                                Toast.makeText(PrivateInfoActivity.this, "프로필 이미지를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }
                    }).show();
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(PrivateInfoActivity.this);
                    dialog.setTitle("로그인 알림")
                            .setMessage("로그인 후 사용가능한 기능입니다.\n로그인 하시겠습니까?")
                            .setNegativeButton("아니요", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(PrivateInfoActivity.this, "취소하였습니다", Toast.LENGTH_SHORT).show();
                                }
                            }).setPositiveButton("예", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(PrivateInfoActivity.this, LoginActivity.class);
                            startActivity(intent);
                        }
                    });
                    dialog.show();
                }
            }
        });

        appinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(PrivateInfoActivity.this, "Version : 1.0.0", Toast.LENGTH_SHORT).show();
            }
        });

        notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateInfoActivity.this, NoticeActivity.class);
                startActivity(intent);
            }
        });

        communityrule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateInfoActivity.this, ShowCommunityRuleActivity.class);
                startActivity(intent);
            }
        });

        inforule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PrivateInfoActivity.this, ShowInfoRuleActivity.class);
                intent.putExtra("menu", "info");
                startActivity(intent);
            }
        });

        license.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseFirestore.getInstance().collection("이용규칙").document("license").get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String data = documentSnapshot.getData().get("value").toString();
                        AlertDialog.Builder builder = new AlertDialog.Builder(PrivateInfoActivity.this)
                                .setTitle("오픈소스 라이센스")
                                .setMessage(data)
                                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PrivateInfoActivity.this)
                                .setTitle("오픈소스 라이센스")
                                .setMessage("로딩 오류")
                                .setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        builder.show();
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(ImagePicker.shouldHandle(requestCode, resultCode, data)) {
            waiting.setVisibility(View.VISIBLE);
            final Uri profile_image = Uri.parse("file://" + ImagePicker.getFirstImageOrNull(data).getPath());

            FirebaseStorage.getInstance().getReferenceFromUrl("gs://kinder-114b0.appspot.com/사용자/프로필")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("image").putFile(profile_image)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {
                            UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                                    .setPhotoUri(taskSnapshot.getDownloadUrl())
                                    .build();

                            FirebaseAuth.getInstance().getCurrentUser().updateProfile(request).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Glide.with(PrivateInfoActivity.this).load(taskSnapshot.getDownloadUrl()).into(profile);
                                    waiting.setVisibility(View.GONE);
                                    Toast.makeText(PrivateInfoActivity.this, "프로필 이미지를 재설정했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    });
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fromleft, R.anim.toright);
    }
}
