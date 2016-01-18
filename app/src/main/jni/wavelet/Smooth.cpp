#include "Smooth.h"
#include <iostream>


void Smooth::copy(double input[], double buf[], int left_bound, int kernel_size){
	for (int i = 0; i < kernel_size; i++){
		buf[i] = input[left_bound + i];
	}
}



//5点3次平滑算法
double* Smooth::linearSmooth5(double in[], int N){
	int i;

	double *out = new double[N];

	if (out == NULL){
		return NULL;
	}

	if (N < 5) {
		for (i = 0; i <= N - 1; i++) {
			out[i] = out[i];
		}
	}
	else {
		out[0] = (3.0 * in[0] + 2.0 * in[1] + in[2] - in[4]) / 5.0;
		out[1] = (4.0 * in[0] + 3.0 * in[1] + 2 * in[2] + in[3]) / 10.0;
		for (i = 2; i <= N - 3; i++) {
			out[i] = (in[i - 2] + in[i - 1] + in[i] + in[i + 1] + in[i + 2]) / 5.0;
		}
		out[N - 2] = (4.0 * in[N - 1] + 3.0 * in[N - 2] + 2.0 * in[N - 3] + in[N - 4]) / 10.0;
		out[N - 1] = (3.0 * in[N - 1] + 2.0 * in[N - 2] + in[N - 3] - in[N - 5]) / 5.0;
	}

	return out;
}

void Smooth::bubbleSort(double data[], int size){
	double temp;
	while (size > 1)
	{
		for (int i = 0; i < size - 1; i++)
		{
			if (data[i] > data[i + 1])
			{
				temp = data[i];
				data[i] = data[i + 1];
				data[i + 1] = temp;
			}
		}
		size--;
	}
}


/**
* @brief medianFilter 中值滤波器
* @param data 输入数据
* @param size 输入数据的元素数量
* @param kernel_size 滤波器大小，最好为奇数
* @param buf 函数内部使用的数组，如果为 NULL 则函数内部分配空间。
*/
void Smooth::medianFilter(double input[], double output[], int size, int kernel_size, double buf[])
{
	bool newBuf = false;
	if (buf == 0)
	{
		newBuf = true;
		buf = new double[kernel_size];
	}
	int half = kernel_size / 2;
	int end = size - half;

	for (int pos = 0; pos < half; pos++)
	{
		int part = 2 * pos + 1;
		copy(input, buf, 0, part);
		bubbleSort(buf, part);
		output[pos] = buf[part / 2];
	}
	int left_bound = 0;
	for (int pos = half; pos < end; pos++)
	{
		copy(input, buf, left_bound, kernel_size);
		left_bound++;
		bubbleSort(buf, kernel_size);
		output[pos] = buf[half];
	}
	left_bound++;
	for (int pos = end; pos < size; pos++)
	{
		int part = 2 * (size - pos) - 1;
		copy(input, buf, left_bound, part);
		left_bound += 2;
		bubbleSort(buf, part);
		output[pos] = buf[part / 2];
	}
	if (newBuf)
	{
		delete[] buf;
	}
}