export function FabricMesh() {
  return (
    <svg
      className="fabric-mesh"
      viewBox="0 0 1440 900"
      preserveAspectRatio="xMidYMid slice"
      aria-hidden="true"
    >
      <defs>
        <linearGradient id="plane" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stopColor="#0d5c63" stopOpacity="0.16" />
          <stop offset="40%" stopColor="#1c2434" stopOpacity="0.05" />
          <stop offset="100%" stopColor="#0b1220" stopOpacity="0.02" />
        </linearGradient>
        <pattern id="grid" width="48" height="48" patternUnits="userSpaceOnUse">
          <path
            d="M48 0H0V48"
            fill="none"
            stroke="#0b1220"
            strokeOpacity="0.05"
            strokeWidth="1"
          />
        </pattern>
      </defs>
      <rect width="1440" height="900" fill="url(#plane)" />
      <rect width="1440" height="900" fill="url(#grid)" />
      <g stroke="#0b1220" strokeOpacity="0.14" strokeWidth="1.25" fill="none">
        <path className="strand s1" d="M120 180C360 120 520 280 720 220C920 160 1100 260 1320 190" />
        <path className="strand s2" d="M80 420C300 340 480 520 740 440C980 360 1160 520 1360 450" />
        <path className="strand s3" d="M140 680C360 580 560 760 780 660C1000 560 1180 740 1340 650" />
        <path d="M420 160 L480 440 L520 700" />
        <path d="M720 220 L740 440 L780 660" />
        <path d="M1020 210 L1000 430 L1080 680" />
      </g>
      <g fill="#0d5c63">
        <circle className="node n1" cx="420" cy="160" r="4" />
        <circle className="node n2" cx="720" cy="220" r="5" />
        <circle className="node n3" cx="1020" cy="210" r="4" />
        <circle className="node n4" cx="480" cy="440" r="4" />
        <circle className="node n5" cx="740" cy="440" r="6" />
        <circle className="node n6" cx="1000" cy="430" r="4" />
        <circle className="node n7" cx="520" cy="700" r="4" />
        <circle className="node n8" cx="780" cy="660" r="5" />
        <circle className="node n9" cx="1080" cy="680" r="4" />
      </g>
    </svg>
  );
}
