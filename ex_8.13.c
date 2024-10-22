#include <stdio.h>
#include <stdlib.h>
#include <math.h>

void delete_matr(double **matr, int n) {
    if (matr == NULL) return;
    for (int i = 0; i < n; i++) {
        free(matr[i]);
    }
    free(matr);
}

double** input_matrix(int n, int m) {
    double** matr = (double**) calloc(n, sizeof(double*));
    for (int i = 0; i < n; i++) {
        matr[i] = (double*) calloc(m, sizeof(double)); // Змінено n на m
        for (int j = 0; j < m; j++) {
            printf("a[%d, %d] = ", i, j);
            scanf("%lf", &matr[i][j]);
        }
    }
    return matr;
}

double** mult_matr(const double **matr1, const double **matr2, int n, int m) {
    double** matr3 = (double**) calloc(n, sizeof(double*));
    for (int i = 0; i < n; i++) {
        matr3[i] = (double*) calloc(m, sizeof(double)); // Змінено n на m
    }
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < m; ++j) {
            matr3[i][j] = 0;
            for (int k = 0; k < m; ++k) { // Правильно використовуємо m
                matr3[i][j] += matr1[i][k] * matr2[k][j];
            }
        }
    }
    return matr3;
}

void output_matr(const double **matr, int n, int m) {
    for (size_t i = 0; i < n; i++) {
        for (size_t j = 0; j < m; j++) {
            printf("%lf ", matr[i][j]);
        }
        printf("\n");
    }
}

int main() {
    int n, m;
    printf("Enter n and m: \n");
    scanf("%d %d", &n, &m);

    printf("Enter matrix 1: \n");
    double** matr1 = input_matrix(n, m);
    printf("Enter matrix 2: \n");
    double** matr2 = input_matrix(m, n);
    double** matr3 = mult_matr(matr1, matr2, n, m);
    printf("Result: \n");
    output_matr(matr3, n, m);

    delete_matr(matr1, n);
    delete_matr(matr2, m);
    delete_matr(matr3, n);
}
