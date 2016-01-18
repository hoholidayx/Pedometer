#ifndef _Included_Process
#define _Included_Process


double h[] = { 0.332670552950, 0.806891509311, 0.459877502118,
-0.135011020010, -0.085441273882, 0.035226291882 };
double g[] = { 0.0352263, 0.08544127, -0.135011,
-0.459877502118, 0.8068915, -0.33267055 };

//Daubechies小波变换
void DWT(double g[], double h[], int wlen, double c[], double d[], int m, int sca[]) 
{
	int i, j, k, mid, flag[20];
	double p, q;

	for (flag[0] = 0, i = 0; i<m; i++)
	{
		flag[i + 1] = flag[i] + sca[i];
	}

	for (j = 1; j <= m; j++)
	{
		for (i = 0; i<sca[j]; i++)
		{
			p = 0;
			q = 0;

			for (k = 0; k<wlen; k++)
			{
				mid = k + 2 * i;
				if (mid >= sca[j - 1])  mid = mid - sca[j - 1];

				p = p + h[k] * c[flag[j - 1] + mid];
				q = q + g[k] * c[flag[j - 1] + mid];
			}

			c[flag[j] + i] = p;
			d[flag[j] + i] = q;
		}
	}

}

//Daubechies小波逆变换
void IDWT(double g[], double h[], int wlen, double c[], double d[], int m, int sca[])
{
	int i, j, k, mid, flag[20];
	double p, q;

	for (flag[0] = 0, i = 0; i<m; i++)
	{
		flag[i + 1] = flag[i] + sca[i];
	}

	for (k = m; k>0; k--)
	{
		for (i = 0; i<sca[k]; i++)
		{
			p = 0;
			q = 0;

			for (j = 0; j<wlen / 2; j++)
			{
				mid = i - j;
				if (mid<0)  mid = sca[k] + (i - j);

				p += h[2 * j] * c[flag[k] + mid] + g[2 * j] * d[flag[k] + mid];
				q += h[2 * j + 1] * c[flag[k] + mid] + g[2 * j + 1] * d[flag[k] + mid];
			}

			c[flag[k - 1] + 2 * i] = p;
			c[flag[k - 1] + 2 * i + 1] = q;
		}
	}

}


#endif