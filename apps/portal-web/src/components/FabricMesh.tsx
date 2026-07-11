export function FabricMesh() {
  return (
    <svg
      className="fabric-mesh"
      viewBox="0 0 1200 800"
      preserveAspectRatio="xMidYMid slice"
      aria-hidden="true"
    >
      <defs>
        <linearGradient id="fabricFade" x1="0%" y1="0%" x2="100%" y2="100%">
          <stop offset="0%" stopColor="#0a7a72" stopOpacity="0.22" />
          <stop offset="45%" stopColor="#1a2438" stopOpacity="0.08" />
          <stop offset="100%" stopColor="#0c1222" stopOpacity="0.04" />
        </linearGradient>
        <radialGradient id="nodeGlow" cx="50%" cy="50%" r="50%">
          <stop offset="0%" stopColor="#0a7a72" stopOpacity="0.35" />
          <stop offset="100%" stopColor="#0a7a72" stopOpacity="0" />
        </radialGradient>
      </defs>
      <rect width="1200" height="800" fill="url(#fabricFade)" />
      <g stroke="#0c1222" strokeOpacity="0.12" strokeWidth="1" fill="none">
        <path d="M80 140 L320 220 L560 120 L840 260 L1120 160" />
        <path d="M60 380 L280 300 L520 420 L780 280 L1100 400" />
        <path d="M100 620 L340 520 L620 640 L900 500 L1140 600" />
        <path d="M320 220 L280 300 L340 520" />
        <path d="M560 120 L520 420 L620 640" />
        <path d="M840 260 L780 280 L900 500" />
        <path d="M320 220 L520 420 L840 260" />
        <path d="M280 300 L560 120 L780 280" />
      </g>
      <g fill="#0a7a72">
        <circle className="node n1" cx="320" cy="220" r="5" />
        <circle className="node n2" cx="560" cy="120" r="5" />
        <circle className="node n3" cx="840" cy="260" r="5" />
        <circle className="node n4" cx="280" cy="300" r="4.5" />
        <circle className="node n5" cx="520" cy="420" r="6" />
        <circle className="node n6" cx="780" cy="280" r="4.5" />
        <circle className="node n7" cx="340" cy="520" r="4.5" />
        <circle className="node n8" cx="620" cy="640" r="5" />
        <circle className="node n9" cx="900" cy="500" r="5" />
      </g>
      <circle cx="520" cy="420" r="48" fill="url(#nodeGlow)" className="pulse" />
      <g fontFamily="var(--font-body)" fontSize="13" fill="#5a667a">
        <text x="300" y="200">Okta</text>
        <text x="840" y="242">SailPoint</text>
        <text x="250" y="288">Entra</text>
        <text x="500" y="448">Identra</text>
        <text x="900" y="486">Saviynt</text>
      </g>
    </svg>
  );
}
