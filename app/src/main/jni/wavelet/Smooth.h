#ifndef _Included_Smooth
#define _Included_Smooth


class Smooth{
private:	
	void copy(double input[], double buf[], int left_bound, int kernel_size);

public:
	static const int DEFAULT_MEDIAN_WINDOW_SIZE = 5;

	void bubbleSort(double data[], int size);
	void medianFilter(double input[], double output[], int size, int kernel_size, double buf[]);

	double* linearSmooth5(double in[], int N);

};

#endif