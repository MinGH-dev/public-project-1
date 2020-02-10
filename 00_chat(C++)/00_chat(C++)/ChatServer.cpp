#include "stdafx.h"
#include "ChatServer.h"

//ChatServer* ChatServer::m_instance = NULL;
//
//ChatServer * ChatServer::GetInstance()
//{
//	if (NULL == m_instance)
//	{
//		m_instance = new ChatServer;
//
//		return m_instance;
//	}
//
//	return nullptr;
//}

ChatServer::ChatServer()
{
	m_listenSocket = INVALID_SOCKET;
	ZeroMemory(&m_wsaData, sizeof(WSADATA));
	// m_instance = NULL; static error
}

ChatServer::~ChatServer()
{
	WSARelease();
}

BOOL ChatServer::Start(WORD wPort)
{
	if (FALSE == WSAInitialize())
	{
		return FALSE;
	}

	// Listen Socket Init
	if (FALSE == CreateListenSocket(wPort))
	{
		return FALSE;
	}

	return 0;
}

BOOL ChatServer::Close()
{
	CloseListenSocket();

	return 0;
}

BOOL ChatServer::WSAInitialize()
{
	WORD wVersionRequested = MAKEWORD(2, 2);
	int nRet = WSAStartup(wVersionRequested, &m_wsaData);

	if (nRet == SOCKET_ERROR)
	{
		// Windows Socket Init Fail
		return FALSE;
	}

	// WinSock Version Check
	if (LOBYTE(m_wsaData.wVersion) != 2
		|| HIBYTE(m_wsaData.wVersion) != 2)
	{
		// Invalid Version
		return FALSE;
	}

	return TRUE;
}

void ChatServer::WSARelease()
{
	// WinSock 정리
	WSACleanup();
}

BOOL ChatServer::CreateListenSocket(WORD wPort)
{
	// Create Socket
	m_listenSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	// if ((m_listenSocket = WSASocket(AF_INET, SOCK_STREAM, IPPROTO_TCP, NULL, 0, WSA_FLAG_OVERLAPPED)) == INVALID_SOCKET)
	if (m_listenSocket == INVALID_SOCKET)
	{
		// Fail Create Listen Socket
		int nError = WSAGetLastError();
		return FALSE;
	}

	// Bind
	struct sockaddr_in addr;
	addr.sin_family = AF_INET;
	addr.sin_port = htons(wPort);
	addr.sin_addr.s_addr = htonl(INADDR_ANY);

	if (SOCKET_ERROR == bind(m_listenSocket, (sockaddr*)&addr, sizeof(addr)))
	{
		// Fail Bind Listen Socket
		int nError = WSAGetLastError();
		return FALSE;
	}

	// Listen
	if (SOCKET_ERROR == listen(m_listenSocket, SOMAXCONN))
	{
		// Fail Listen
		int nError = WSAGetLastError();
		return FALSE;
	}

	return TRUE;
}

BOOL ChatServer::CloseListenSocket()
{
	closesocket(m_listenSocket);
	m_listenSocket = INVALID_SOCKET;

	return 0;
}

void ChatServer::OnAccept()
{
	SOCKADDR_IN clientAddr;
	int nClientAddrSize = sizeof(clientAddr);
	SOCKET clientSocket = accept(m_listenSocket, (SOCKADDR*)&clientAddr, &nClientAddrSize);
	// Aceept하면 SOCKET 생성
	// 얘네를 가지고 Client 관리

	// 받는거
	char recvBuf[1024] = { 0, };
	int nRecvSize = recv(clientSocket, recvBuf, sizeof(recvBuf), 0);

	/*WSABUF wsaBuf;
	DWORD dwRecvByte = 0;
	OVERLAPPED overlapped;
	WSARecv(clientSocket, &wsaBuf, 1, &dwRecvByte, 0, &overlapped, NULL);*/

	// 보내는거
	send(clientSocket, recvBuf, nRecvSize, 0);
}

BOOL ChatServer::OnRecv()
{
	return 0;
}

BOOL ChatServer::OnSend()
{
	return 0;
}

unsigned int ChatServer::WorkerThread(void * pParam)
{
	return 0;
}