import random
import sympy as sp
import sys

def generate_multiplication_and_division(lower_bound, upper_bound, rating):
    questions = []
    for i in range(lower_bound + 1, upper_bound):
        for j in range(i + 1, upper_bound):
            human_question = "What's the result of " + str(i) + "*" + str(j) + "?"
            latex_equation =  str(i) + "*" + str(j) + "="
            multiplication_question = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating,
                "answer": i * j
            }

            human_question = "What's the result of " + str(i*j) + "/" + str(j) + "?"
            latex_equation =  str(i*j) + "/" + str(j) + "="
            division_question1 = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating,
                "answer": i
            }

            human_question = "What's the result of " + str(i*j) + "/" + str(i) + "?"
            latex_equation =  str(i*j) + "/" + str(i) + "="
            division_question2 = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating,
                "answer": j
            }

            # generate negative numbers too
            human_question = "What's the result of " + str(-1 * i) + "*" + str(j) + "?"
            latex_equation =  str(-1 * i) + "*" + str(j) + "="
            multiplication_question_negative = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating + 200,
                "answer": -1 * i * j
            }

            human_question = "What's the result of " + str(-1 * i*j) + "/" + str(j) + "?"
            latex_equation =  str(-1 * i*j) + "/" + str(j) + "="
            division_question1_negative = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating + 200,
                "answer": -1 * i
            }

            human_question = "What's the result of " + str(-1 * i * j) + "/" + str(i) + "?"
            latex_equation =  str(-1 * i*j) + "/" + str(i) + "="
            division_question2_negative = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating + 200,
                "answer": -1 * j
            }

            questions.append(multiplication_question)
            questions.append(division_question1)
            questions.append(division_question2)

            questions.append(multiplication_question_negative)
            questions.append(division_question1_negative)
            questions.append(division_question2_negative)
    print(questions)
    return questions

generate_multiplication_and_division(lower_bound = int(sys.argv[1]), upper_bound = int(sys.argv[2]), rating = int(sys.argv[3]))