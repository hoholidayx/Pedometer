#include "Process.h"
#include "Wavelet.h"
#include <iostream>

int dataLength = 0;//数据长度


Wavelet::Wavelet(){

}


void Wavelet::reset(){
	memset(d, 0, sizeof(double)*MAX_SIZE);
	memset(c, 0, sizeof(double)*MAX_SIZE);

	dataLength = 0;

	IS_DWT = IS_IDWT = false;
}

void Wavelet::inputData(double dataIn[], int m_dataLength){
	reset();

	if (dataLength <= MAX_SIZE){
		dataLength = m_dataLength;
	}
	else{
		//TODO 改用异常处理
		dataLength = MAX_SIZE;
	}
	

	memcpy(c, dataIn, sizeof(double)*dataLength);
}


void Wavelet::doDWT(){
	if (dataLength == 0 || IS_IDWT){
		return;
	}

	int sca[20], flag[20];
	int j = dataLength;
	flag[0] = 0;

	for (int i = 0; i <= m; i++){
		flag[i + 1] = flag[i] + j;
		sca[i] = j;
		j = j / 2;
	}

	DWT(g, h, wlen, c, d, m, sca);

	IS_DWT = true;
}


void Wavelet::doIDWT(){
	if (dataLength == 0 || !IS_DWT){
		return;
	}

	int sca[20], flag[20];
	int j = dataLength;
	flag[0] = 0;

	for (int i = 0; i <= m; i++)
	{
		flag[i + 1] = flag[i] + j;
		sca[i] = j;
		j = j / 2;
	}

	for (int i = 0; i<sca[m]; i++)
	{
		c[flag[m] + i] = c[flag[m] + i];
	}

	/*保留分解出的第三层*/
	int i = 0;
	int tmpDataLength = 0, tmp = 0;

	double low = 1.0, high = 0.5;

	//前两层权值为0
	for (int k = 1; k <= 2; k++){
		tmpDataLength = 0;
		for (i = 1; i <= k; i++)
		{
			tmp = 1 << i;
			tmpDataLength += dataLength * 2 / tmp;
		}
		for (i = 0; i <dataLength / tmp; i++)
		{
			c[i + tmpDataLength] = c[i + tmpDataLength] * 0;
			d[i + tmpDataLength] = d[i + tmpDataLength] * 0;

		}
	}
	
	//计算第三层
	tmpDataLength = 0;

	for (i = 1; i <= 3; i++)
	{
		tmp = 1 << i;
		tmpDataLength += dataLength * 2 / tmp;
	}
	for (i = 0; i <dataLength / tmp; i++)
	{
		c[i + tmpDataLength] = c[i + tmpDataLength] * low;
		d[i + tmpDataLength] = d[i + tmpDataLength] * high;
	}


	IDWT(g, h, wlen, c, d, m, sca);

	IS_IDWT = true;
}


double* Wavelet::getResult(){
	if (dataLength == 0){
		return NULL;
	}

	double *result = new double[dataLength];

	if (result == NULL){
		//内存分配错误
		//TODO 改用异常处理
		return NULL;
	}

	memcpy(result, c, dataLength*sizeof(double));

	return result;
}

