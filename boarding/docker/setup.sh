#!/bin/bash

echo "Creating DynamoDB Tables on Existing Container"
DYNAMODB_ENDPOINT="http://localhost:8000"
CARS_TABLE="local-cars"
CLASSIFICATION_TABLE="local-classification"
RECEIVER_TABLE="local-receiver"

create_cars_table() {
  aws dynamodb describe-table --endpoint-url $DYNAMODB_ENDPOINT --table $CARS_TABLE --output yaml --no-cli-pager

  if [[ $? != 254 ]]; then
    echo "deleting table definition: ${COURSE_TABLE} in order to re-create"
    aws dynamodb delete-table --endpoint-url $DYNAMODB_ENDPOINT --table-name $CARS_TABLE --output yaml --no-cli-pager
  fi

  echo "creating table definition: ${COURSE_TABLE}"
  aws dynamodb create-table --endpoint-url $DYNAMODB_ENDPOINT --table-name $CARS_TABLE --no-cli-pager \
    --attribute-definitions AttributeName=pk,AttributeType=S \
    AttributeName=sk,AttributeType=S \
    AttributeName=pk,AttributeType=S \
    --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
  aws dynamodb describe-table --endpoint-url $DYNAMODB_ENDPOINT --table $CARS_TABLE --output yaml --no-cli-pager
}

create_classification_table() {
  aws dynamodb describe-table --endpoint-url $DYNAMODB_ENDPOINT --table $CLASSIFICATION_TABLE --output yaml --no-cli-pager

  if [[ $? != 254 ]]; then
    echo "deleting table definition: ${CLASSIFICATION_TABLE} in order to re-create"
    aws dynamodb delete-table --endpoint-url $DYNAMODB_ENDPOINT --table-name $CLASSIFICATION_TABLE --output yaml --no-cli-pager
  fi

  echo "creating table definition: ${CLASSIFICATION_TABLE}"
  aws dynamodb create-table --endpoint-url $DYNAMODB_ENDPOINT --table-name $CLASSIFICATION_TABLE --no-cli-pager \
    --attribute-definitions AttributeName=pk,AttributeType=S \
    AttributeName=sk,AttributeType=S \
    AttributeName=pk,AttributeType=S \
    --key-schema AttributeName=pk,KeyType=HASH AttributeName=sk,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
  aws dynamodb describe-table --endpoint-url $DYNAMODB_ENDPOINT --table $CARS_TABLE --output yaml --no-cli-pager
}

create_receiver_table() {

  aws dynamodb describe-table --endpoint-url $DYNAMODB_ENDPOINT --table $RECEIVER_TABLE --output yaml --no-cli-pager

  if [[ $? != 254 ]]; then
    echo "deleting table definition: ${RECEIVER_TABLE} in order to re-create"
    aws dynamodb delete-table --endpoint-url $DYNAMODB_ENDPOINT --table-name $RECEIVER_TABLE --output yaml --no-cli-pager
  fi

  echo "creating table definition: ${RECEIVER_TABLE}"
  aws dynamodb create-table --endpoint-url $DYNAMODB_ENDPOINT --table-name $RECEIVER_TABLE --no-cli-pager \
    --attribute-definitions AttributeName=course_id,AttributeType=S \
    AttributeName=scope_key,AttributeType=S \
    AttributeName=id,AttributeType=S \
    --key-schema AttributeName=course_id,KeyType=HASH AttributeName=scope_key,KeyType=RANGE \
    --provisioned-throughput ReadCapacityUnits=10,WriteCapacityUnits=5 \
  aws dynamodb describe-table --endpoint-url $DYNAMODB_ENDPOINT --table $RECEIVER_TABLE --output yaml --no-cli-pager
}

populate_receiver_table () {
  echo "populating receiver table with course records"
  aws dynamodb put-item \
    --endpoint-url $DYNAMODB_ENDPOINT \
    --table-name $RECEIVER_TABLE \
    --item '{
              "id": {"S": "d67577d9-fe6b-3e06-a3fd-d67275c46899"},
              "course_id": {"S": "10ff3fbb-105c-40a6-9deb-7e4a466253d6"},
              "scope_key": {"S": "DISTRICT:10839#SCHOOL:35104#SECTION:03820fa5-f2e2-48c6-bfd3-16fd54f7e340#ENROLLMENT:1c503474-2346-444f-9934-7e34241c3c3f"},
              "parent_course_id": {"S": "d67577d9-fe6b-3e06-a3fd-d67275c46879"},
              "district_id": {"S": "10839"},
              "information": {
                  "M": {
                      "name": {"S": "Sample Algebra I Lessons"},
                      "description": {"S": "This full-year course focuses on five critical areas: relationships between quantities and reasoning with equations, linear and exponential relationships, descriptive statistics, expressions and equations, and quadratic functions and modeling. This course builds on the foundation set in middle grades by deepening students understanding of linear and exponential functions and developing fluency in writing and solving one-variable equations and inequalities."},
                      "length": {"S": "Semester"},
                      "subject": {"S": "Math"},
                      "course_tools": {"L": [{"S": "Geometry Handbook"}, {"S": "Chemistry Formula Sheet"}]}
                  }
              },
              "options": {
                  "M": {
                    "advanced_assessment_options": {
                      "M": {
                        "exam": {
                          "M": {
                            "review_mode": {
                              "S": "SHOW_ANSWERS_ONLY"
                            },
                            "save_and_exit": {
                              "M": {
                                "allow_save_and_exit": {
                                  "BOOL": true
                                },
                                "hide_viewed_questions": {
                                  "BOOL": true
                                }
                              }
                            },
                            "unlock_assessments": {
                              "M": {
                                "require_attempt_start": {
                                  "BOOL": true
                                },
                                "require_attempt_start_minutes": {
                                  "N": "15"
                                },
                                "require_for_each_attempt": {
                                  "BOOL": true
                                },
                                "require_to_unlock_assessments": {
                                  "BOOL": true
                                }
                              }
                            }
                          }
                        },
                        "quiz": {
                          "M": {
                            "review_mode": {
                              "S": "SHOW_CORRECT_ANSWERS"
                            },
                            "save_and_exit": {
                              "M": {
                                "allow_save_and_exit": {
                                  "BOOL": false
                                },
                                "hide_viewed_questions": {
                                  "BOOL": false
                                }
                              }
                            },
                            "unlock_assessments": {
                              "M": {
                                "require_attempt_start": {
                                  "BOOL": false
                                },
                                "require_attempt_start_minutes": {
                                  "N": "30"
                                },
                                "require_for_each_attempt": {
                                  "BOOL": false
                                },
                                "require_to_unlock_assessments": {
                                  "BOOL": true
                                }
                              }
                            }
                          }
                        },
                        "test": {
                          "M": {
                            "review_mode": {
                              "S": "SHOW_ANSWERS_ONLY"
                            },
                            "save_and_exit": {
                              "M": {
                                "allow_save_and_exit": {
                                  "BOOL": true
                                },
                                "hide_viewed_questions": {
                                  "BOOL": false
                                }
                              }
                            },
                            "unlock_assessments": {
                              "M": {
                                "require_attempt_start": {
                                  "BOOL": false
                                },
                                "require_attempt_start_minutes": {
                                  "N": "40"
                                },
                                "require_for_each_attempt": {
                                  "BOOL": true
                                },
                                "require_to_unlock_assessments": {
                                  "BOOL": true
                                }
                              }
                            }
                          }
                        }
                      }
                    },
                    "assessment_attempts": {
                      "M": {
                        "automatic_progress": {
                          "NULL": true
                        },
                        "initial_allowed_attempts": {
                          "M": {
                            "exam": {
                              "N": "2"
                            },
                            "quiz": {
                              "N": "2"
                            },
                            "test": {
                              "N": "2"
                            }
                          }
                        },
                        "max_allowed_attempts": {
                          "M": {
                            "exam": {
                              "N": "5"
                            },
                            "quiz": {
                              "N": "5"
                            },
                            "test": {
                              "N": "5"
                            }
                          }
                        }
                      }
                    },
                    "basic_assessment": {
                      "M": {
                        "passing_thresholds": {
                          "M": {
                            "exam": {
                              "N": "70"
                            },
                            "quiz": {
                              "N": "70"
                            },
                            "test": {
                              "N": "70"
                            }
                          }
                        },
                        "time_limits": {
                          "M": {
                            "exam": {
                              "N": "180"
                            },
                            "quiz": {
                              "N": "60"
                            },
                            "test": {
                              "N": "120"
                            }
                          }
                        }
                      }
                    },
                    "free_movement": {
                      "M": {
                        "allow_free_movement": {
                          "BOOL": true
                        },
                        "allow_playback_speed_increase": {
                          "BOOL": false
                        }
                      }
                    },
                    "grade_weights": {
                      "M": {
                        "additional": {
                          "N": "0"
                        },
                        "assignment": {
                          "N": "20"
                        },
                        "exam": {
                          "N": "20"
                        },
                        "project": {
                          "N": "10"
                        },
                        "quiz": {
                          "N": "20"
                        },
                        "test": {
                          "N": "30"
                        }
                      }
                    },
                    "pacing": {
                      "M": {
                        "show_activities_past_due_alert": {
                          "BOOL": true
                        },
                        "show_due_dates": {
                          "BOOL": true
                        },
                        "show_estimated_completion_time": {
                          "BOOL": true
                        },
                        "show_pacing": {
                          "BOOL": false
                        }
                      }
                    },
                    "pretesting": {
                      "M": {
                        "lesson_pretesting": {
                          "BOOL": false
                        },
                        "passing_threshold": {
                          "N": "60"
                        },
                        "save_and_exit": {
                          "M": {
                            "allow_save_and_exit": {
                              "BOOL": false
                            },
                            "hide_viewed_questions": {
                              "NULL": true
                            }
                          }
                        },
                        "time_limit": {
                          "N": "60"
                        },
                        "unlock_assessments": {
                          "M": {
                            "require_attempt_start": {
                              "NULL": true
                            },
                            "require_attempt_start_minutes": {
                              "NULL": true
                            },
                            "require_for_each_attempt": {
                              "NULL": true
                            },
                            "require_to_unlock_assessments": {
                              "BOOL": false
                            }
                          }
                        }
                      }
                    },
                    "start_target_dates": {
                      "M": {
                        "auto_generate_target_date": {
                          "BOOL": false
                        },
                        "days_to_lock_course": {
                          "N": "7"
                        },
                        "lock_course_after_start_date": {
                          "BOOL": false
                        },
                        "lock_course_before_start_date": {
                          "BOOL": false
                        },
                        "number_of_days_to_auto_generate_target_date": {
                          "N": "7"
                        },
                        "term_type": {
                          "S": "FIXED"
                        }
                      }
                    },
                    "student_resource_availability": {
                      "M": {
                        "allow_e_notes": {
                          "M": {
                            "exam": {
                              "BOOL": true
                            },
                            "quiz": {
                              "BOOL": true
                            },
                            "test": {
                              "BOOL": false
                            }
                          }
                        },
                        "display_guided_notes": {
                          "BOOL": false
                        }
                      }
                    }
                  }
                },
              "resources": {
                  "M": {
                     "syllabus": {
                        "M":{
                            "course_goals": {
                               "M":{
                                  "goals": {
                                      "L" : [
                                        { "M" : {
                                          "bullet_point": { "S": "Sample course goal"}
                                            }
                                        },
                                        { "M" : {
                                          "bullet_point": { "S": "Sample course goal"}
                                            }
                                        }
                                      ]
                                  }
                              }
                            },
                            "student_expectations": {
                               "M":{
                                  "description":  { "S": "This course requires the same level of commitment from you as a traditional classroom course would. Throughout the course, you are expected to spend approximately 5–7 hours per week online on the following activities:" },
                                  "expectations": { "L" : [
                                      { "M" : {
                                          "bullet_point": { "S": "Interactive lessons that include a mixture of instructional videos and tasks"}
                                            }
                                        },
                                      { "M" : {
                                          "bullet_point": { "S": "Assignments in which you apply and extend learning in each lesson"}
                                            }
                                        }
                                  ]}
                              }
                            },
                            "communication": {"S": "Your teacher will communicate with you regularly through discussions, in-system messaging, and announcements. You will also communicate with classmates, either via online tools or face to face, as you collaborate on projects, ask and answer questions in your peer group, and develop your speaking and listening skills."},
                            "grading_policy": {"S": "You will be graded on the work you do online and the work you submit electronically to your teacher. The weighting for each category of graded activity is listed below."},
                            "scope_sequence": {"S": "The Course Map provides an interactive scope and sequence of all the topics you will study in this course. The units of study are summarized below."},
                            "last_updated": {"S": "2022-02-18T19:51:35.605706Z"}
                        }
                     }
                  }
              },
              "audit": {
                  "M": {
                      "created_by": {"S": "admin"},
                      "created_time": {"S": "2022-02-18T19:51:35.605706Z"},
                      "updated_by": {"S": "admin"},
                      "updated_time": {"S": "2022-02-18T19:51:35.605711Z"}
                  }
              }
          }'
}

populate_cars_table() {
  echo "populating cars table with records"
  aws dynamodb put-item \
    --endpoint-url $DYNAMODB_ENDPOINT \
    --table-name $COURSE_CUSTOMIZATION_TABLE \
    --item '{
              "pk": {
                "S": "DISTRICT:12345"
              },
              "sk": {
                "S": "ROOT:0de6845e-bde4-4717-982a-bb8c0dacd1dd"
              },
              "district_id": {
                "S": "12345"
              },
              "school_id": {
                "S": "67890"
              },
              "section_id": {
                "S": "17155806-9875-4c27-9112-67d3c3decdb5"
              },
              "enrollment_id": {
                "S": "db0c7264-f820-46bb-a8d4-9ea0ab1f6420"
              },
              "organization_level": {
                "S": "Enrollment"
              },
              "course_id": {
                "S": "008737ea-57f7-4df4-b83e-f28ac9084ad8"
              },
              "customization_root_id": {
                "S": "0de6845e-bde4-4717-982a-bb8c0dacd1dd"
              },
              "published_date": {
                "S": "2023-03-20T03:53:11Z"
              },
              "published_by": {
                "S": "b352f339-8c6c-4dea-abed-67c7abff67bd"
              },
              "status": {
                "S": "INACTIVE"
              },
              "type": {
                "S": "ROOT"
              },
              "customization_edition": {
                "N": "1"
              },
              "gsi_edition_pk": {
                "S": "DISTRICT:12345"
              },
              "gsi_edition_sk": {
                "S": "ENROLLMENT:db0c7264-f820-46bb-a8d4-9ea0ab1f6420#SECTION:17155806-9875-4c27-9112-67d3c3decdb5#SCHOOL:67890#DISTRICT:12345#COURSE:008737ea-57f7-4df4-b83e-f28ac9084ad8#EDITION:1"
              }
            }'
}

populate_classification_table() {
  echo "populating classification table with records"
  aws dynamodb put-item \
    --endpoint-url $DYNAMODB_ENDPOINT \
    --table-name $CLASSIFICATION_TABLE \
    --item '{
                    "pk": {
                      "S": "LESSON:08652a78-486d-4977-a708-d213f4ab0769"
                    },
                    "sk": {
                      "S": "LESSON:08652a78-486d-4977-a708-d213f4ab0769#EDITION:11985"
                    },
                    "pk_edition_position": {
                      "S": "LESSON:08652a78-486d-4977-a708-d213f4ab0769#EDITION:11985"
                    },
                    "sk_edition_position": {
                      "S": "LESSON:08652a78-486d-4977-a708-d213f4ab0769#EDITION:11985"
                    },
                    "pk_latest_position": {
                      "S": "LESSON:08652a78-486d-4977-a708-d213f4ab0769"
                    },
                    "sk_latest_position": {
                      "S": "LESSON:08652a78-486d-4977-a708-d213f4ab0769#EDITION:11985"
                    },
                    "activity_count": {
                      "S": "1"
                    },
                    "completion_time": {
                      "S": "30"
                    },
                    "course_alias": {
                      "S": "PUBLISHED"
                    },
                    "course_edition???": {
                      "S": "100399"
                    },
                    "course_element_type": {
                      "S": "LESSON"
                    },
                    "course_row_count": {
                      "S": "`-1"
                    },
                    "depth": {
                      "S": "3"
                    },
                    "edition": {
                      "S": "11985"
                    },
                    "id": {
                      "S": "08652a78-486d-4977-a708d213f4ab0769"
                    },
                    "instance_id": {
                      "S": "1a543235-d848-4ae0-ab03-6da75db532f8"
                    },
                    "internal_title": {
                      "S": "Introduction to Marketing"
                    },
                    "lesson_count": {
                      "S": "0"
                    },
                    "metadata": {
                      "M": {}
                    },
                    "position": {
                      "S": "000-004-001"
                    },
                    "saved_to_db_at": {
                      "S": "2023-12-27T10:59:34.619961788Z"
                    },
                    "sequence": {
                      "S": "1"
                    },
                    "split_count": {
                      "S": "0"
                    },
                    "src_identifier": {
                      "S": "c6faab00-3299-11e3-931d-bc764e043e0c"
                    },
                    "teacher_scored_count": {
                      "S": "0"
                    },
                    "title": {
                      "S": "Introduction to Marketing"
                    },
                    "type": {
                      "S": "LESSON"
                    },
                    "unit_count": {
                      "S": "0"
                    }
                  }'
}

create_tables() {
  create_classification_table
  create_cars_table
  create_receiver_table
}

populate_tables() {
  populate_cars_table
  populate_receiver_table
  populate_classification_table
}

create_tables
populate_tables

sleep 100