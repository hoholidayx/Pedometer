#ifndef _Included_Wavelet
#define _Included_Wavelet


#define MAX_SIZE 1024 * 6

class Wavelet{
private:

	//数据数组
	double c[MAX_SIZE], d[MAX_SIZE];

	int m = 3;//分解的层数
	int wlen = 6;//小波的长度

	bool IS_DWT = false;//是否已经分解
	bool IS_IDWT = false;//是否已经重构

	void reset();

public:
	Wavelet();
	void inputData(double dataIn[], int m_dataLength);
	void doDWT();
	void doIDWT();
	double* getResult();
};

#endif