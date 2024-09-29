#include <stdio.h>
#include <math.h>
double task_a(unsigned int n) {
    double res = 0;
    for (int i = 1; i <= n; i++) {
        res += sqrt(2 + res);
    }
    return res;
}

double task_b(unsigned int n) {
    unsigned int p;
    double res = 0;
    p = n;
    for (int i = 1; i < n; i++) {
        res += sqrt(3 * p);
        p -= 1;
    }
    return res;
}

int main() {
    unsigned int n;
    printf("n = \n");
    scanf("%u", &n);
    double res = task_a(n);
    printf("res = %lf\n", res);
    double res_2 = task_b(n);
    printf("res_2 = %lf", res_2);
}
