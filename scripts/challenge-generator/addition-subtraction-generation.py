import random
import sympy as sp
import sys

def generate_addition_and_subtraction(lower_bound, upper_bound, rating):
    questions = []
    for i in range(lower_bound + 1, upper_bound):
        for j in range(lower_bound + 1, upper_bound):
            human_question = "What's the result of " + str(i) + "+" + str(j) + "?"
            latex_equation =  str(i) + "+" + str(j) + "="
            addition_question = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating,
                "answer": i + j
            }

            human_question = "What's the result of " + str(i) + "-" + str(j) + "?"
            latex_equation =  str(i) + "-" + str(j) + "="
            subtraction_question = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating,
                "answer": i - j
            }

            # generate negative numbers too
            human_question = "What's the result of " + str(-1 * i) + "-" + str(j) + "?"
            latex_equation =  str(-1 * i) + "-" + str(j) + "="
            subtraction_question_negative = {
                "human_question": human_question,
                "latex_equation": latex_equation,
                "initial_rating": rating + 200,
                "answer": -1 * i - j
            }

            questions.append(addition_question)
            questions.append(subtraction_question)
            questions.append(subtraction_question_negative)
    print(questions)
    return questions

generate_addition_and_subtraction(lower_bound = int(sys.argv[1]), upper_bound = int(sys.argv[2]), rating = int(sys.argv[3]))