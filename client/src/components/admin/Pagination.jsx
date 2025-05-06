import { IoChevronBack, IoChevronForward } from "react-icons/io5";

function Pagination({ currentPage, totalPages, onPageChange }) {
  return (
    <div className="mt-4 flex justify-center gap-1">
      <button
        onClick={() => onPageChange(Math.max(0, currentPage - 1))}
        disabled={currentPage === 0}
        className="p-1 text-gray-600 border border-gray-200 rounded hover:bg-gray-50 disabled:opacity-30 disabled:hover:bg-transparent transition-colors"
      >
        <IoChevronBack size={16} />
      </button>
      {[...Array(totalPages)].map((_, index) => {
        if (
          index === 0 ||
          index === totalPages - 1 ||
          (index >= currentPage - 2 && index <= currentPage + 2)
        ) {
          return (
            <button
              key={index}
              onClick={() => onPageChange(index)}
              className={`px-2 py-1 text-sm rounded transition-colors ${
                currentPage === index
                  ? "bg-gray-100 text-gray-900 border border-gray-300"
                  : "text-gray-600 border border-gray-200 hover:bg-gray-50"
              }`}
            >
              {index + 1}
            </button>
          );
        } else if (index === currentPage - 3 || index === currentPage + 3) {
          return (
            <span key={index} className="px-1 text-gray-400">
              ...
            </span>
          );
        }
        return null;
      })}
      <button
        onClick={() => onPageChange(Math.min(totalPages - 1, currentPage + 1))}
        disabled={currentPage === totalPages - 1}
        className="p-1 text-gray-600 border border-gray-200 rounded hover:bg-gray-50 disabled:opacity-30 disabled:hover:bg-transparent transition-colors"
      >
        <IoChevronForward size={16} />
      </button>
    </div>
  );
}

export default Pagination;
