// 00_chat(C++).cpp: 콘솔 응용 프로그램의 진입점을 정의합니다.
//

#include "stdafx.h"


int main()
{
	// Main 
	while (true)
	{
		char buf[100] = { 0, };
		scanf_s("%s", buf, sizeof(buf));

		if (0 == strcmp(buf, "shutdown"))
		{
			break;
		}
	}

    return 0;
}

