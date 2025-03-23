#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_SIZE 100

// ����ջ�ṹ
typedef struct {
    char data[MAX_SIZE];
    int top;
} Stack;

// ��ʼ��ջ
void initStack(Stack* s) {
    s->top = -1;
}

// �ж�ջ�Ƿ�Ϊ��
int isEmpty(Stack* s) {
    return s->top == -1;
}

// �ж�ջ�Ƿ�����
int isFull(Stack* s) {
    return s->top == MAX_SIZE - 1;
}

// ��ջ
void push(Stack* s, char c) {
    if (!isFull(s)) {
        s->data[++(s->top)] = c;
    }
}

// ��ջ
char pop(Stack* s) {
    if (!isEmpty(s)) {
        return s->data[(s->top)--];
    }
    return '\0';
}

// ��ȡջ��Ԫ��
char peek(Stack* s) {
    if (!isEmpty(s)) {
        return s->data[s->top];
    }
    return '\0';
}

// �ж��Ƿ�Ϊ�����
int isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
}

// ��ȡ��������ȼ�
int getPriority(char c) {
    switch (c) {
        case '+':
        case '-':
            return 1;
        case '*':
        case '/':
            return 2;
        case '^':
            return 3;
        default:
            return 0;
    }
}

// ��׺���ʽת��׺���ʽ
void infixToPostfix(char* infix, char* postfix) {
    Stack s;
    initStack(&s);
    int i = 0, j = 0;
    char c;

    while ((c = infix[i]) != '\0') {
        // ��������ֻ���ĸ��ֱ�����
        if (isalnum(c)) {
            postfix[j++] = c;
        }
        // ����������ţ�ѹ��ջ
        else if (c == '(') {
            push(&s, c);
        }
        // ����������ţ�����ջ�е������ֱ������������
        else if (c == ')') {
            while (!isEmpty(&s) && peek(&s) != '(') {
                postfix[j++] = pop(&s);
            }
            if (!isEmpty(&s) && peek(&s) == '(') {
                pop(&s); // ����������
            }
        }
        // ����������
        else if (isOperator(c)) {
            while (!isEmpty(&s) && peek(&s) != '(' && 
                   getPriority(peek(&s)) >= getPriority(c)) {
                postfix[j++] = pop(&s);
            }
            push(&s, c);
        }
        i++;
    }

    // ����ջ��ʣ��������
    while (!isEmpty(&s)) {
        postfix[j++] = pop(&s);
    }
    postfix[j] = '\0';
}

int main() {
    char infix[MAX_SIZE];
    char postfix[MAX_SIZE];

    printf("��������׺���ʽ: ");
    scanf("%s", infix);

    infixToPostfix(infix, postfix);
    printf("��׺���ʽ: %s\n", postfix);

    return 0;
}
