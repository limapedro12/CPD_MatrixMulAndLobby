#include <stdio.h>
#include <iostream>
#include <iomanip>
#include <time.h>
#include <cstdlib>
#include <papi.h>

using namespace std;

#define SYSTEMTIME clock_t
 
void OnMult(int m_ar, int m_br) 
{
	
	SYSTEMTIME Time1, Time2;
	
	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;

	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);

    
	Time1 = clock();

	for(i=0; i<m_ar; i++)
	{	for( j=0; j<m_br; j++)
		{	temp = 0;
			for( k=0; k<m_ar; k++)
			{	
				temp += pha[i*m_ar+k] * phb[k*m_br+j];
			}
			phc[i*m_ar+j]=temp;
		}
	}

    Time2 = clock();
	sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1) / CLOCKS_PER_SEC);
	cout << st;

	// display 10 elements of the result matrix to verify correctness
	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
}

void OnMultLine(int m_ar, int m_br)
{
	SYSTEMTIME Time1, Time2;
	
	char st[100];
	double temp;
	int i, j, k;

	double *pha, *phb, *phc;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));
	fill_n(phc, m_ar*m_ar, 0);

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;

	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);

    
	Time1 = clock();

	for(i=0; i<m_ar; i++) {
		for(k=0; k<m_ar; k++) {
			for(j=0; j<m_br; j++) {	
				phc[i*m_ar+j] += pha[i*m_ar+k] * phb[k*m_br+j];
			}
		}
	}

    Time2 = clock();

	sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1) / CLOCKS_PER_SEC);
	cout << st;

	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
}

void main_to_sub_matrix(int starting_i, int starting_j, double* large_matrix, int large_size, double* ret, int ret_size){
	for(int i=0; i<ret_size; i++) {
		for(int j=0; j<ret_size; j++) {	
			ret[i*ret_size + j] = large_matrix[(starting_i+i)*large_size+(starting_j+j)];
		}
	}
}

void sub_to_main_matrix(int starting_i, int starting_j, double* large_matrix, int large_size, double* ret, int ret_size){
	for(int i=0; i<ret_size; i++) {
		for(int j=0; j<ret_size; j++) {	
			large_matrix[(starting_i+i)*large_size+(starting_j+j)] = ret[i*ret_size + j];
		}
	}
}

void multiply_square_matrix(double *m1, double *m2, double *res, int size) {
	for(int i=0; i<size; i++) {
		for(int k=0; k<size; k++) {
			for(int j=0; j<size; j++) {	
				res[i*size+j] += m1[i*size+k] * m2[k*size+j];
			}
		}
	}
}

void add_square_matrix(double *m1, double *m2, double *res, int size) {
	for(int i=0; i<size; i++) {
		for(int j=0; j<size; j++) {	
			res[i*size+j] = m1[i*size+j] + m2[i*size+j];
		}
	}
}

void print_square_matrix(double *m, int size) {
	for(int i=0; i<size; i++) {
		for(int j=0; j<size; j++) {	
			cout << m[i*size+j] << " ";
		}
		cout << endl;
	}
}

void OnMultBlock(int m_ar, int m_br, int bkSize)
{
	SYSTEMTIME Time1, Time2;
	
	char st[100];
	double temp;
	int i, j, k, numberOfBlocks, i2, j2, k2;

	double *pha, *phb, *phc, *temp1, *temp2, *temp3, *temp4;
		
    pha = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phb = (double *)malloc((m_ar * m_ar) * sizeof(double));
	phc = (double *)malloc((m_ar * m_ar) * sizeof(double));
	fill_n(phc, m_ar*m_ar, 0);

	temp1 = (double *)malloc((bkSize * bkSize) * sizeof(double));
	temp2 = (double *)malloc((bkSize * bkSize) * sizeof(double));
	temp3 = (double *)malloc((bkSize * bkSize) * sizeof(double));
	temp4 = (double *)malloc((bkSize * bkSize) * sizeof(double));

	for(i=0; i<m_ar; i++)
		for(j=0; j<m_ar; j++)
			pha[i*m_ar + j] = (double)1.0;

	for(i=0; i<m_br; i++)
		for(j=0; j<m_br; j++)
			phb[i*m_br + j] = (double)(i+1);

	numberOfBlocks = m_ar/bkSize;

	Time1 = clock();
	for(i=0; i<m_ar; i+=bkSize) {
		for(k=0; k<m_ar; k+=bkSize) {
			for(j=0; j<m_ar; j+=bkSize) {
				main_to_sub_matrix(i, j, phc, m_ar, temp1, bkSize);
				main_to_sub_matrix(i, k, pha, m_ar, temp2, bkSize);
				main_to_sub_matrix(k, j, phb, m_ar, temp3, bkSize);

				fill_n(temp4, bkSize * bkSize, 0);
				multiply_square_matrix(temp2, temp3, temp4, bkSize);

				fill_n(temp2, bkSize * bkSize, 0);
				add_square_matrix(temp1, temp4, temp2, bkSize);

				sub_to_main_matrix(i, j, phc, m_ar, temp2, bkSize);
			}
		}
	}

    Time2 = clock();

	sprintf(st, "Time: %3.3f seconds\n", (double)(Time2 - Time1) / CLOCKS_PER_SEC);
	cout << st;

	cout << "Result matrix: " << endl;
	for(i=0; i<1; i++)
	{	for(j=0; j<min(10,m_br); j++)
			cout << phc[j] << " ";
	}
	cout << endl;

    free(pha);
    free(phb);
    free(phc);
}

void handle_error (int retval)
{
  printf("PAPI error %d: %s\n", retval, PAPI_strerror(retval));
  exit(1);
}

void init_papi() {
  int retval = PAPI_library_init(PAPI_VER_CURRENT);
  if (retval != PAPI_VER_CURRENT && retval < 0) {
    printf("PAPI library version mismatch!\n");
    exit(1);
  }
  if (retval < 0) handle_error(retval);

  std::cout << "PAPI Version Number: MAJOR: " << PAPI_VERSION_MAJOR(retval)
            << " MINOR: " << PAPI_VERSION_MINOR(retval)
            << " REVISION: " << PAPI_VERSION_REVISION(retval) << "\n";
}

int main (int argc, char *argv[])
{
	char c;
	int lin, col, blockSize;
	int op;
	int n_min, n_max, inc;
	
	int EventSet = PAPI_NULL;
  	long long values[5];
  	int ret;
	
	ret = PAPI_library_init( PAPI_VER_CURRENT );
	if ( ret != PAPI_VER_CURRENT )
		std::cout << "FAIL" << endl;


	ret = PAPI_create_eventset(&EventSet);
	if (ret != PAPI_OK) cout << "ERROR: create eventset" << endl;

	ret = PAPI_add_event(EventSet,PAPI_L1_DCM );
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L1_DCM" << endl;

	ret = PAPI_add_event(EventSet,PAPI_L2_DCM);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L2_DCM" << endl;
	
	ret = PAPI_add_event(EventSet,PAPI_L3_LDM);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_L3_LDM" << endl;
	
	ret = PAPI_add_event(EventSet,PAPI_DP_OPS);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_DP_OPS" << endl;
	
	ret = PAPI_add_event(EventSet,PAPI_TOT_INS);
	if (ret != PAPI_OK) cout << "ERROR: PAPI_TOT_INS" << endl;

	op=1;
	do {
		cout << endl << "1. Multiplication" << endl;
		cout << "2. Line Multiplication" << endl;
		cout << "3. Block Multiplication" << endl;
		cout << "0. Exit" << endl;
		cout << "Selection?: ";
		cin >> op;
		if (op == 0)
			break;

		cout << endl << "Minimum dimension: lins=cols?: ";
		cin >> n_min;
		cout << endl << "Maximum dimension: lins=cols?: ";
		cin >> n_max;
		cout << endl << "Step?: ";
		cin >> inc;
		cout << endl;

		switch (op){
			case 1: 
				for (int n = n_min; n <= n_max; n+=inc) {
					ret = PAPI_start(EventSet);
					if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;
					
					cout << endl << "Dimensions: " << n << '*' << n << endl;
					lin = n;
					col = n;
					OnMult(lin, col);
					
					ret = PAPI_stop(EventSet, values);
			  		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;
			  		printf("L1 DCM: %lld \n",values[0]);
			  		printf("L2 DCM: %lld \n",values[1]);
			  		printf("L3 LDM: %lld \n",values[2]);
			  		printf("DP OPS: %lld \n",values[3]);
			  		printf("TOT INS: %lld \n", values[4]);

					ret = PAPI_reset( EventSet );
					if ( ret != PAPI_OK )
						std::cout << "FAIL reset" << endl; 
				}
				break;
			case 2:
				for (int n = n_min; n <= n_max; n+=inc) {
					ret = PAPI_start(EventSet);
					if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;
					
					cout << endl << "Dimensions: " << n << '*' << n << endl;
					lin = n;
					col = n;
					OnMultLine(lin, col);
					
					ret = PAPI_stop(EventSet, values);
			  		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;
			  		printf("L1 DCM: %lld \n",values[0]);
			  		printf("L2 DCM: %lld \n",values[1]);
			  		printf("L3 LDM: %lld \n",values[2]);
			  		printf("DP OPS: %lld \n",values[3]);
			  		printf("TOT INS: %lld \n", values[4]);

					ret = PAPI_reset( EventSet );
					if ( ret != PAPI_OK )
						std::cout << "FAIL reset" << endl; 
				}
				break;
			case 3:
				cout << "Block size?: ";
				cin >> blockSize;
				for (int n = n_min; n <= n_max; n+=inc) {
					ret = PAPI_start(EventSet);
					if (ret != PAPI_OK) cout << "ERROR: Start PAPI" << endl;
					
					cout << endl << "Dimensions: " << n << '*' << n << endl;
					lin = n;
					col = n;
					OnMultBlock(lin, col, blockSize);
					
					ret = PAPI_stop(EventSet, values);
			  		if (ret != PAPI_OK) cout << "ERROR: Stop PAPI" << endl;
			  		printf("L1 DCM: %lld \n",values[0]);
			  		printf("L2 DCM: %lld \n",values[1]);
			  		printf("L3 LDM: %lld \n",values[2]);
			  		printf("DP OPS: %lld \n",values[3]);
			  		printf("TOT INS: %lld \n", values[4]);

					ret = PAPI_reset( EventSet );
					if ( ret != PAPI_OK )
						std::cout << "FAIL reset" << endl; 
				}
				break;

		}
	} while (op != 0);

	ret = PAPI_remove_event( EventSet, PAPI_L1_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl; 

	ret = PAPI_remove_event( EventSet, PAPI_L2_DCM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;
	
	ret = PAPI_remove_event( EventSet, PAPI_L3_LDM );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;
	
	ret = PAPI_remove_event( EventSet, PAPI_DP_OPS );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;
	
	ret = PAPI_remove_event( EventSet, PAPI_TOT_INS );
	if ( ret != PAPI_OK )
		std::cout << "FAIL remove event" << endl;

	ret = PAPI_destroy_eventset( &EventSet );
	if ( ret != PAPI_OK )
		std::cout << "FAIL destroy" << endl;
}
