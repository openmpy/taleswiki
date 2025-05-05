import React from "react";

function Sidebar() {
  return (
    <aside className="w-64 bg-white p-6 rounded-lg border border-gray-200">
      <h3 className="text-lg font-semibold mb-4">사이드바</h3>
      <ul className="space-y-2">
        <li className="py-2 border-b border-gray-200">메뉴 1</li>
        <li className="py-2 border-b border-gray-200">메뉴 2</li>
        <li className="py-2 border-b border-gray-200">메뉴 3</li>
      </ul>
    </aside>
  );
}

export default Sidebar;
