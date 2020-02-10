#pragma once
class ChatServer
{
private:
	WSADATA m_wsaData; // Windows Socket DLL

	SOCKET m_listenSocket; // æÍ∏¶ ≈Î«ÿº≠ listen

//	static ChatServer* m_instance; // singleton
//
//public:
//	ChatServer* GetInstance(); // singleton

public:
	ChatServer();
	~ChatServer();

public:
	BOOL Start(WORD wPort);
	BOOL Close();

private:
	BOOL WSAInitialize();
	void WSARelease();

	// Listen Socket Init
	BOOL CreateListenSocket(WORD wPort);
	// Listen Socket Release
	BOOL CloseListenSocket();

public:
	void OnAccept();
	BOOL OnRecv();
	BOOL OnSend();

	unsigned int __stdcall WorkerThread(void* pParam);
};

