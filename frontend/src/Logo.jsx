export default function Logo({ size = 42 }) {
  return (
    <img
      className="logo-mark"
      src="/logo.jpg"
      width={size}
      height={size}
      alt="CDMS"
    />
  );
}

export function homePath(user) {
  return user?.role === "CUSTOMER" ? "/app/showroom" : "/app";
}
