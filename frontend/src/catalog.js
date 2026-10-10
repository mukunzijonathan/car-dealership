export function brandsOf(cars) {
  return ["All", ...Array.from(new Set(cars.map((c) => c.brand))).sort()];
}

export function filterCars(cars, { query = "", brand = "All", band = "all", sort = "featured" } = {}) {
  let list = cars;
  if (brand !== "All") list = list.filter((c) => c.brand === brand);
  const q = query.trim().toLowerCase();
  if (q) {
    list = list.filter((c) => `${c.brand} ${c.model} ${c.vin} ${c.manufactureYear}`.toLowerCase().includes(q));
  }
  if (band === "under30") list = list.filter((c) => c.price < 30000000);
  if (band === "mid") list = list.filter((c) => c.price >= 30000000 && c.price < 70000000);
  if (band === "over70") list = list.filter((c) => c.price >= 70000000);
  const copy = [...list];
  if (sort === "price-asc") copy.sort((a, b) => a.price - b.price);
  else if (sort === "price-desc") copy.sort((a, b) => b.price - a.price);
  else if (sort === "year-desc") copy.sort((a, b) => b.manufactureYear - a.manufactureYear);
  else copy.sort((a, b) => {
    const rank = { AVAILABLE: 0, RESERVED: 1, SOLD: 2 };
    return (rank[a.status] ?? 9) - (rank[b.status] ?? 9) || b.price - a.price;
  });
  return copy;
}

export function featuredCars(cars, count = 3) {
  return [...cars]
    .filter((c) => c.status === "AVAILABLE")
    .sort((a, b) => b.price - a.price)
    .slice(0, count);
}
