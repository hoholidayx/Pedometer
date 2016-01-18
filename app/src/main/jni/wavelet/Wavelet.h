#ifndef _Included_Wavelet
#define _Included_Wavelet


#define MAX_SIZE 1024 * 6

class Wavelet{
private:

	//��������
	double c[MAX_SIZE], d[MAX_SIZE];

	int m = 3;//�ֽ�Ĳ���
	int wlen = 6;//С���ĳ���

	bool IS_DWT = false;//�Ƿ��Ѿ��ֽ�
	bool IS_IDWT = false;//�Ƿ��Ѿ��ع�

	void reset();

public:
	Wavelet();
	void inputData(double dataIn[], int m_dataLength);
	void doDWT();
	void doIDWT();
	double* getResult();
};

#endif