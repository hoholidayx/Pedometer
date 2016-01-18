#include "wavelet_utils_Wavelet.h"
#include "Wavelet.h"
#include "Smooth.h"
#include <iostream>

JNIEXPORT jdoubleArray  JNICALL Java_wavelet_utils_Wavelet_linearSmooth5
(JNIEnv *env, jobject obj, jdoubleArray dataIn, jint N){
	Smooth smooth;

	double *data = new double[N];
	//����ת��
	env->GetDoubleArrayRegion(dataIn, 0, N, data);

	//���ݼ���
	double *result = smooth.linearSmooth5(data, N);
	
	jdoubleArray m_result;
	if (result != NULL){
	    m_result = env->NewDoubleArray(N);
	}
	else{
		return NULL;
	}
	

	//���ݿ���java��
	env->SetDoubleArrayRegion(m_result, 0, N, result);

	if (result == NULL){
		delete[] result;
	}

	return m_result;

}



JNIEXPORT jdoubleArray  JNICALL Java_wavelet_utils_Wavelet_waveletFilter
(JNIEnv *env, jobject obj, jdoubleArray dataIn, jint N){
	Wavelet wavelet;

	double *data = new double[N];
	//����ת��
	env->GetDoubleArrayRegion(dataIn, 0, N, data);

	//���ݼ���
	wavelet.inputData(data, N);
	wavelet.doDWT();
	wavelet.doIDWT();
	double *result = wavelet.getResult();

	jdoubleArray m_result = env->NewDoubleArray(N);

	//���ݿ���java��
	env->SetDoubleArrayRegion(m_result, 0, N, result);

	if (result == NULL){
		delete[] result;
	}

	return m_result;
}

JNIEXPORT jdoubleArray JNICALL Java_wavelet_utils_Wavelet_medianFilter
(JNIEnv *env, jobject obj, jdoubleArray dataIn, jint N){
	Smooth smooth;

	double *data = new double[N];
	//����ת��
	env->GetDoubleArrayRegion(dataIn, 0, N, data);

	double* result = new double[N];

	smooth.medianFilter(data, result, N, Smooth::DEFAULT_MEDIAN_WINDOW_SIZE, NULL);

	jdoubleArray m_result;
	if (result != NULL){
		m_result = env->NewDoubleArray(N);
	}
	else{
		return NULL;
	}

	//���ݿ���java��
	env->SetDoubleArrayRegion(m_result, 0, N, result);

	if (result == NULL){
		delete[] result;
	}

	return m_result;
}