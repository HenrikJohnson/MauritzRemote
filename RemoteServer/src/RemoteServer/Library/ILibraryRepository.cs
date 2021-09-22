using RemoteServer.Library.Catalog;
using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace RemoteServer.Library
{
    public interface ILibraryRepository
    {
        Task<List<QueueItem>> GetQueue(String room, LibraryQueue queue);

        Task<List<QueueItem>> AddQueueItem(String room, LibraryQueue queue, String songId);

        Task<List<QueueItem>> DeleteQueueItem(String room, LibraryQueue queue, int queueId);

        Task<List<QueueItem>> MoveQueueItem(String room, LibraryQueue queue, int queueId, int afterQueueId);

        Task<List<LibraryItem>> Query(LibraryQueue queue, LibrarySearchType type, String search, int? offset, int? limit);

        Task<AmazonCatalog<T>> Catalog<T>(String type, Func<AmazonRawData, T> converter) where T : AmazonEntity;
    }
}